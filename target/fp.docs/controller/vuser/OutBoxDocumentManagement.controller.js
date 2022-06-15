sap.ui.controller("controller.vuser.OutBoxDocumentManagement", {

	/**
	 * Called when a controller is instantiated and its View controls (if
	 * available) are already created. Can be used to modify the View before it
	 * is displayed, to bind event handlers and do other one-time
	 * initialization.
	 * 
	 * @memberOf controller.vuser.SharedDocumentList
	 */
	onInit: function() {
		this.getView().setModel(new sap.ui.model.json.JSONModel(), "outbox");
		this.getView().setModel(new sap.ui.model.json.JSONModel(), "members");
		this.getView().setModel(new sap.ui.model.json.JSONModel(), "documentRequest");
		this.getView().setModel(new sap.ui.model.json.JSONModel(), "activity");
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this);
		AppHelper.initProfileDetails(this);
	},
	loadRequiredData: function() {
		AppHelper.showLoader()
		this.getView().getModel('outbox').setData(Connection.requestData(Configuration.vendorUserAccountServiceUrl.documentOutBox));
		AppHelper.hideLoader();
		this.defaultButtonVisibility();
	},
	/**
	 * handle routing
	 */
	onRouteMatched: function(oEvent) {
		var sName = oEvent.getParameter('name');
		if (sName === "outbound_share") {
			this.loadRequiredData();
			this._showFragment("OutBoxDocumentList");
			this.updateBreadCrumb(true, "documentListPanel");
			this.byId("documentList").removeSelections(true);
		} else {
			console.log("Wrong Path" + sName);
			return;
		}
	},
	resolveDraftState: function(oStatus) {
		return Formatter.isDraft(oStatus) ? "DRAFT" : "";
	},
	/**
	 * handle request update 
	 */
	handleEdit: function(oEvent) {
		var oSelectedItem = this.byId('documentList').getSelectedItem();
		var oDocument = this.getView().getModel("outbox").getProperty(oSelectedItem.getBindingContext('outbox').sPath);
		try {
			var oArguments = {
				"fragmentName": "DocumentShareRequest",
				args: {
					action: "UPDATE",
					documentShareID: oDocument.documentShareID
				}
			};
			//navigate 
			this.router.navTo('document_share', {
				arguments: JSON.stringify(oArguments)
			});
		} catch (e) {
			console.log(e.message);
		}

	},
	_fragments: {},

	_getFragment: function(sFragmentName) {
		var oFragment = this._fragments[sFragmentName];
		if (oFragment) {
			return oFragment;
		}
		oFragment = sap.ui.xmlfragment(this.getView().getId(), "view.vuser." + sFragmentName, this);
		this.getView().addDependent(oFragment);
		return this._fragments[sFragmentName] = oFragment;
	},

	_showFragment: function(sFragmentName) {
		var oPage = this.getView().byId("documentListPage");
		oPage.removeAllContent();
		oPage.insertContent(this._getFragment(sFragmentName));
	},
	/**
	 * Handle the click events
	 */
	onItemClickEvent: function(oEvent) {
		this.oDocument = this.getView().getModel('outbox').getProperty(oEvent.getSource().getBindingContext('outbox').sPath);
		var isFolder = this.oDocument.contentType === "doc/folder";
		if (isFolder) {
			this.currentFolder = this.getView().getModel('outbox').getProperty(oEvent.getSource().getBindingContext('outbox').sPath);
			this.loadLocationContent(this.oDocument.openCmisID, this.oDocument.openCmisParentID, this.oDocument.contentType);
		} else {
			this.initDownload(this.oDocument);
		}
	},

	onItemSelectedEvent: function(oEvent) {
		this.oDocument = this.getView().getModel('outbox').getProperty(oEvent.getSource().getSelectedItem().getBindingContext('outbox').sPath);
		var isFolder = Formatter.isFolder(this.oDocument.contentType);
		this.toggleButton(isFolder);
		if (!isFolder) {
			this.toggleUploadButton(true);
		}
        this.toggleDraftButtons(Formatter.isDraft(this.oDocument.requestStatus));
	},
	toggleDraftButtons: function(isDraft) {
		this.getView().byId('btnDelete').setVisible(isDraft);
		this.getView().byId('btnEdit').setVisible(isDraft);
	},
	toggleButton: function(isFolder) {
		this.getView().byId('btnDownload').setVisible(!isFolder);
		this.getView().byId('btnDetails').setVisible(isFolder);
		this.getView().byId('btnUpload').setVisible(isFolder);
	},
	toggleUploadButton: function(isVisible) {
		this.getView().byId('btnUpload').setVisible(isVisible);
	},
	defaultButtonVisibility: function() {
		this.getView().byId('btnDownload').setVisible(false);
		this.getView().byId('btnDetails').setVisible(false);
		this.getView().byId('btnUpload').setVisible(false);
		this.toggleDraftButtons(false);
	},
	toggleInFolderButton: function() {
		this.toggleUploadButton(true);
		this.getView().byId('btnDownload').setVisible(false);
		this.getView().byId('btnDetails').setVisible(false);
		this.toggleDraftButtons(false);
	},

	updateWithContextItems: function(sPath) {
		jQuery.sap.require("sap.m.MessageToast");
		this.data = this.oModel.getProperty(sPath, this.oModel);
		console.log(this.data.contentType);
		if (this.data.contentType === "doc/folder") {
			this.getView().getModel().setData(this.data);
		}
	},
	/**
	 * Handle the creation of document share
	 * @param oEvent
	 */
	onCreateDocument: function(oEvent) {
		try {
			var oArguments = {
				"fragmentName": "DocumentShareRequest",
				args: {
					action: "CREATE"
				}
			};
			//navigate 
			this.router.navTo('document_share', {
				arguments: JSON.stringify(oArguments)
			});
		} catch (e) {
			console.log(e.message);
		}
	},
	loadLocationContent: function(oOpenCmisDocumentID, oOpenCmisDocumentParent, oContentType) {
		var that = this;
		var data = {
			documentID: oOpenCmisDocumentID,
			parentID: oOpenCmisDocumentParent
		};
		var url = Configuration.vendorUserAccountServiceUrl.documentQuery;
		Connection.postData(url, data,
			function(response) {
				if (response.result.length > 0) {
					that.getView().getModel('outbox').setData(response);
					var searchField = that.byId("sharedSearchInput");
					searchField.setValue("");
					searchField.fireLiveChange();
					that.getView().byId("documentList").removeSelections();
					that.toggleInFolderButton();
					that.updateBreadCrumb(false, 'documentListPanel');
				} else {
					sap.m.MessageToast.show("No items found");
				}

			},
			function(xhr, textStatus, errorThrown) {
				if (xhr.responseJSON.resultTyp !== "SUCCESS") {
					sap.m.MessageToast.show(xhr.responseJSON.result);
				}
			});
	},
	/**
	 * Handle download
	 */
	handleDownload: function(oEvent) {
		var sPath = this.byId('documentList').getSelectedItem().getBindingContext("outbox").sPath;
		var oDocument = this.getView().getModel('outbox').getProperty(sPath);
		this.initDownload(oDocument);
	},
	initDownload: function(oDocument) {
		var url = Configuration.vendorUserAccountServiceUrl.documentDownload;
		Connection.dowloadRequest(oDocument, url);
	},

	/**
	 * Handle show details
	 */
	handleDetail: function(oEvent) {
		try {
			AppHelper.showLoader();
			var that = this;
			var sPath = this.byId('documentList').getSelectedItem().getBindingContext('outbox').sPath;
			var oSelectedDocument = this.getModel('outbox').getProperty(sPath);
			var url = Configuration.vendorUserAccountServiceUrl.queryDocumentMembers + oSelectedDocument.documentShareID;
			Connection.requestWithCallbackData(url, function(data) {
				AppHelper.hideLoader();
			}, function(data) {
				oSelectedDocument["documentSizeMB"] = data.relMap.size;
				oSelectedDocument["createdBy"] = that.getCreatedBy();
				that.getModel('activity').setData({
					"logs": data.relMap.log
				});
				that.getModel('documentRequest').setData(oSelectedDocument);
				that.getModel('members').setData(data)
				that._showFragment("OutBoxRequestDetail");
				that.updateBreadCrumb(false, "outboxRequestDetailPanel");
				that.toggleDetailsButtons();
				AppHelper.hideLoader();
			});
		} catch (e) {
			console.log(e);
			AppHelper.hideLoader();
		}
	},

	getCreatedBy: function() {
		var profile = AppHelper.profile().getData();
		return profile.firstName + " " + profile.lastName;
	},
	onFilterRequest: function(oEvent) {
		var aFilters = [];
		var sQuery = oEvent.getSource().getValue();
		if (sQuery && sQuery.length > 0) {
			var filter = new sap.ui.model.Filter("documentName", sap.ui.model.FilterOperator.Contains, sQuery);
			aFilters.push(filter);
		}
		var list = this.getView().byId("documentList");
		var binding = list.getBinding("items");
		binding.filter(aFilters, "Application");
	},
	getUploadDialog: function() {
		if (!this.oUploadDialog) {
			// create dialog via fragment factory
			this.oUploadDialog = sap.ui.xmlfragment("view.all.UploadDialog", this);
			// connect dialog to view (models, lifecycle)
			this.getView().addDependent(this.oUploadDialog);
		}
		return this.oUploadDialog;
	},
	getModel: function(oModelName) {
		var oModel = this.getView().getModel(oModelName);
		if (!oModel) {
			oModel = new sap.ui.model.json.JSONModel();
			sap.ui.getCore().setModel(oModel, oModelName);
		}
		return oModel;
	},

	onCloseDialog: function(oEvent) {
		this.getUploadDialog().close();
	},
	/**
	 * @memberOf controller.vuser.DocumentShare
	 * @param oEvent
	 */
	handleUploadPress: function(oEvent) {
		AppHelper.showLoader();
		var oFileUploader = sap.ui.getCore().byId("docShareFileUploader");
		var expiryTenure = this.getView().getModel("req").getData().expiryTenure;
		if (expiryTenure < 1 || expiryTenure > 30) {
			AppHelper.displayMsgAlert("Availability period for document is limited to 30days!");
			AppHelper.hideLoader();
		} else {
			if (!oFileUploader.getValue()) {
				sap.m.MessageToast.show("Please choose file");
				AppHelper.hideLoader();
				return;
			}
			oFileUploader.upload();
		}
	},

	/**
	 * Handle Upload on completion
	 * @memberOf controller.vuser.SharedDocumentList
	 * @param oEvent
	 */
	handleUploadComplete: function(oEvent) {
		var sResponse = oEvent.getParameter("responseRaw");
		if (sResponse) {
			var oResponse = JSON.parse(sResponse);
			if (oResponse.resultType === 'SUCCESS') {
				this.getUploadDialog().close();
				var data = this.getView().getModel("req").getData();
				if (data.showTenureInput) {
					this.loadLocationContent(this.currentFolder.openCmisID, this.currentFolder.openCmisParentID, this.currentFolder.contentType);
				}
			} else {
				sap.m.MessageToast.show(oResponse.result);
			}
			AppHelper.hideLoader();
		}
		sap.ui.getCore().byId("docShareFileUploader").setValue("");
	},
	handleUpload: function(oEvent) {
		var oRequestModel = new sap.ui.model.json.JSONModel();
		this.getView().setModel(oRequestModel, "req");
		var oSelectedItem = this.byId('documentList').getSelectedItem();
		var data = {};
		if (!oSelectedItem) {
			var openCmisID = this.currentFolder.openCmisID;
			data = {
				openCmisID: openCmisID,
				showTenureInput: true
			};
		} else {
			var oDocument = this.getView().getModel("outbox").getProperty(oSelectedItem.getBindingContext('outbox').sPath);
			if (Formatter.isFolder(oDocument.contentType)) {
				var openCmisID = oDocument.openCmisID;
				data = {
					openCmisID: openCmisID,
					showTenureInput: true
				};
				this.currentFolder = oDocument;
			} else {
				var openCmisID = this.currentFolder.openCmisID;
				data = {
					openCmisID: openCmisID,
					showTenureInput: true
				};
			}
		}
		oRequestModel.setData(data);
		this.getUploadDialog().open();
	},
	deleteDocument: function(onProceedDeletion) {
		sap.m.MessageBox.show(AppHelper.i18n().getProperty("msg_delete_document"), {
			icon: sap.m.MessageBox.Icon.WARNING,
			title: AppHelper.i18n().getProperty("title_delete_document"),
			actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
			onClose: function(oAction) {
				if (oAction === sap.m.MessageBox.Action.YES) {
					return onProceedDeletion();
				}
			}
		});
	},
	handleDelete: function(oEvent) {
		var oSelectedItem = this.byId('documentList').getSelectedItem();
		var oDocument = this.getView().getModel("outbox").getProperty(oSelectedItem.getBindingContext('outbox').sPath);
		var that = this;
		this.deleteDocument(function() {
			var seletedDocumentIDs = {
				collection: []
			};
			seletedDocumentIDs.collection.push({
				documentID: oDocument.openCmisID
			});
			var url = Configuration.vendorUserAccountServiceUrl.documentDelete;
			Connection.postData(url, seletedDocumentIDs,
				function(response) {
					if (oDocument.contentType === "doc/folder") {
						that.defaultLoad();
					} else {
						that.loadLocationContent(oDocument.openCmisParentID, oDocument.openCmisParentID, oDocument.contentType);
					}
				},
				function(xhr, textStatus, errorThrown) {
					sap.m.MessageToast.show(xhr.responseJSON.result);
				});
		});

	},
	/**
	 * Display the user profile
	 * @param oEvent
	 */
	onProfileShowEvent: function(oEvent) {
		AppHelper.displayProfileDetails(this, oEvent);
	},
	/**
	 * Handle system logout
	 * @param oEvent
	 */
	onLogoutRequest: function(oEvent) {
		Connection.logout(Configuration.home);
	},
	isPasscodeSecured: function(oRestrictions) {
		if (!oRestrictions) {
			oRestrictions = this.oDocument.restriction || {};
		}
		return oRestrictions.hasOwnProperty("PassCodeSecure");
	},
	isWriteAllowed: function(oRestrictions) {
		if (!oRestrictions) {
			oRestrictions = this.oDocument.restriction || {};
		}
		return oRestrictions.hasOwnProperty("AllowReadAndWrite");
	},
	isLimitedAvailability: function(oRestrictions) {
		if (!oRestrictions) {
			oRestrictions = this.oDocument.restriction || {};
		}
		return oRestrictions.hasOwnProperty("LimitAvailability");
	},
	isDownloadable: function(oRestrictions) {
		if (!oRestrictions) {
			oRestrictions = this.oDocument.restriction || {};
		}
		return oRestrictions.hasOwnProperty("AllowDownload");
	},
	/* 
	 * @param oCount
	 */
	formatCount: function(oCount) {
		if (oCount === 0) {
			return "";
		}
		return oCount;
	},
	sharedBy: function(oSharedBy) {
		if (!oSharedBy) {
			return "";
		}
		return oSharedBy;

	},
	/* 
	 * @param oCount
	 */
	arrayCount: function(value) {
		if (value) {
			return value.length;
		} else {
			return 0;
		}
	},

	restrictionCount: function(oRestrictions) {
		var count = 0;
		if (oRestrictions) {
			count = Object.keys(oRestrictions).length;
		}
		return count;
	},
	/**
	 * 
	 */
	updateBreadCrumb: function(isBase, targetLocationID) {
		var sInboxHeader = AppHelper.i18n().getProperty("hdr_outbox_doc");
		if (!isBase) {
			var sDocumentName = this.oDocument.documentName;
			var oRootLabel = new sap.m.Label({
				text: sInboxHeader,
				design: "Bold"
			});
			var pathIcon = new sap.ui.core.Icon({
				src: "sap-icon://slim-arrow-right"
			});
			var infoToolBar = new sap.m.OverflowToolbar({
				design: "Solid",
				content: [oRootLabel, pathIcon, new sap.m.Label({
					text: sDocumentName,
					design: "Bold"
				})]
			});
			this.byId(targetLocationID).setInfoToolbar(infoToolBar);
			this.toggleBackButton(true);
		} else {
			var oRootLabel = new sap.m.Label({
				text: sInboxHeader,
				design: "Bold"
			});
			var infoToolBar = new sap.m.OverflowToolbar({
				design: "Solid",
				content: [oRootLabel]
			});
			this.byId(targetLocationID).setInfoToolbar(infoToolBar);
			this.toggleBackButton(false);
		}
	},
	onPressBackButton: function(oEvent) {
		this._showFragment("OutBoxDocumentList");
		this.updateBreadCrumb(true, "documentListPanel");
		this.defaultButtonVisibility();
		this.byId("documentList").removeSelections(true);
	},
	/**
	 * 
	 * @param oEvent
	 */
	onBackPress: function(oEvent) {
		this.defaultLoad();
	},
	toggleBackButton: function(isFolder) {
		this.getView().byId('shared_docs_back').setVisible(isFolder);
	},
	toggleDetailsButtons: function() {
		this.defaultButtonVisibility();
		this.toggleBackButton(false);
	},
	onProfileClosePress: function(oEvent) {
		AppHelper.closeProfileDetails(this);
	},
	defaultLoad: function() {
		this.updateBreadCrumb(true, "documentListPanel");
		this.loadRequiredData();
		this.defaultButtonVisibility();
		this.byId("documentList").removeSelections(true);
	}
});