sap.ui.controller("controller.admin.ManageVendors", {

	/**
	 * Called when a controller is instantiated and its View controls (if
	 * available) are already created. Can be used to modify the View before it
	 * is displayed, to bind event handlers and do other one-time
	 * initialization.
	 * 
	 * @memberOf controller.admin.ManageVendors
	 */
	onInit: function() {

		this.oInitialLoadFinishedDeferred = jQuery.Deferred();

		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this);
		if (sap.ui.Device.system.phone) {
			// Do not wait for the master when in mobile phone resolution
			this.oInitialLoadFinishedDeferred.resolve();
		}

	},
	actions: {
		edit: "edit",
		create: "create",
		none: "none"
	},
	onRouteMatched: function(oEvent) {
		var sName = oEvent.getParameter("name");
		if (sName !== 'admin_vendors') {
			return;
		} else {
			this._showFragment("VendorList");
			this.loadRequiredData();
		}
	},
	loadRequiredData: function() {
		AppHelper.initProfileDetails(this);
		this.getView().setBusy(true);
		this.initializeVendorsModel();
		this.getModel('vc').setData(Connection.requestData(Configuration.manageSetupServiceUrl.vendorCategory));
		//this.getModel('quota').setData(Connection.requestData(Configuration.manageQuotaServiceUrl.queryQuotaAllocations)); // JR
		this.getModel("status").setData(sap.ui.getCore().getModel('status').getData());
		this.getModel("salutation").setData(sap.ui.getCore().getModel('salutation').getData());
		this.getView().setBusy(false);

	},
	/**
	 * Get Current model
	 * 
	 * @param oModelName
	 */
	getModel: function(oModelName) {
		var oModel = this.getView().getModel(oModelName);
		if (!oModel) {
			oModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(oModel, oModelName);
		}
		return oModel;
	},
	/**
	 * Called when the new vendor button is clicked.
	 * 
	 * @memberOf controller.admin.ManageVendors
	 */
	onPressCreateNewVendor: function(oEvent) {
		this.toggleToSaveButton();
		this.oVendorData = {};
		this.oVendorData["action"] = this.actions.create;
		var vendorData = this.oVendorData;
		vendorData.isEdit = (vendorData.action === this.actions.edit);
		vendorData.isNew = (vendorData.action === this.actions.create);
		vendorData.userID = "admin";
		this.getModel('vendor').setData(vendorData);
		this._showFragment("ManageVendorData");
	},
	
	/**
	 * On Change User ID and Company Name
	 */
	onChangePopulateVendorList : function(oEvent){
		var objId = oEvent.getSource().getId();
		var val = oEvent.getParameters().value.trim();
		if (oEvent.oSource.mProperties.name !== "companyName"){
		sap.ui.getCore().getControl(objId).setValueState(sap.ui.core.ValueState.None);
		var regex = /[a-zA-Z]{0,9}\@/;
		if (!regex.test(val)) {
			AppHelper.displayAlertMsg(AppHelper.messageType.error,"Incorrect Value"); 
			sap.ui.getCore().getControl(objId).setValueState(sap.ui.core.ValueState.Error);
			sap.ui.getCore().getControl(objId).setValue("");
		}
		}
		var vendorModel = this.getView().getModel("vendor");
		var createData = vendorModel.getData();
		if(createData.userID){
			createData.vendorCode = createData.userID.split("@")[1];
		}
		
		if(createData.vendorCode && createData.companyName){
			var tempList = createData.addVendorsList;
			var initialElement = {"vendorCode" : createData.vendorCode,"vendorName":createData.companyName,"isEntryEnabled" : false};
			tempList = (tempList instanceof Array)? tempList : [];
			if(tempList.length>0){
				tempList[0] = initialElement;
			}else{
				tempList.push(initialElement);
			}
			createData.addVendorsList = tempList
		}
	},
	
	/**
	 * Check Pattern
	 */
	checkPattern : function(oEvent){
		var val = oEvent.getParameters().value.trim();
		var objId = oEvent.getSource().getId();
		sap.ui.getCore().getControl(objId).setValueState(sap.ui.core.ValueState.None);
		var idx = oEvent.getSource().getParent().getBindingContextPath();
		idx = idx.charAt(idx.lastIndexOf('/') + 1);
		if (val && isNaN(val)) {
			AppHelper.displayAlertMsg(AppHelper.messageType.error,"Only Numeric Value Is Allowed");
			sap.ui.getCore().getControl(objId).setValueState(sap.ui.core.ValueState.Error);
			sap.ui.getCore().getControl(objId).setValue("");
		}
		var tmpData = JSON.stringify(this.getView().getModel("vendor").getData().addVendorsList);
		tmpData = JSON.parse(tmpData);
		if (idx !== -1) {
			tmpData.splice(idx, 1);
		}		
		for ( var k=0;k<tmpData.length;k++){
			if ( val === tmpData[k].vendorCode ){
				AppHelper.displayAlertMsg(AppHelper.messageType.error,"Duplicate Entry Made For Vendor Code");	
				sap.ui.getCore().getControl(objId).setValueState(sap.ui.core.ValueState.Error);
				sap.ui.getCore().getControl(objId).setValue("");				
			}
		}
	},
	getUploadDialog: function() {
		if (!this.oUploadDialog) {
			// create dialog via fragment factory
			this.oUploadDialog = sap.ui.xmlfragment("view.admin.UploadDialog", this);
			// connect dialog to view (models, lifecycle)
			this.getView().addDependent(this.oUploadDialog);
		}
		return this.oUploadDialog;
	},

	onCreateVendorPressBulk: function() {
		this.getUploadDialog().open();
	},

	onVendorCloseDialog: function() {
		this.getUploadDialog().close();
	},

	handleAllowDocumentShare: function(oEvent) {
		var oSwitch = oEvent.getSource();
		var oAllowDocumentShare = oSwitch.getState();
		var oVendor = this.getModel("vendor").getData();
		var data = {
			vendorID: oVendor.vendorID,
			allowDocumentShare: oAllowDocumentShare
		};
		var url = Configuration.manageVendorServiceUrl.allowAdminDocumentShare;
		Connection.postData(url, data,
			function(response) {},
			function(xhr, textStatus, errorThrown) {
				oSwitch.setState(false);
				var result = xhr.responseJSON.result;
				if (typeof result !== "undefined") {
					AppHelper.displayMsgAlert(result);
				} else {
					AppHelper.displayMsgAlert("Error occured");
				}
			});

	},
	/**
	 * @memberOf controller.vuser.DocumentShare
	 * @param oEvent
	 */
	handleVendorUploadPress: function(oEvent) {

		var oFileUploader = sap.ui.getCore().byId("vendorFileUploader");
		if (!oFileUploader.getValue()) {
			sap.m.MessageToast.show("Please choose file");
			return;
		}
		AppHelper.showLoader();
		oFileUploader.upload();
	},

	/**
	 * Handle Upload on completion
	 * 
	 * @memberOf controller.vuser.DocumentShare
	 * @param oEvent
	 */
	handleVendorUploadComplete: function(oEvent) {
		var sResponse = oEvent.getParameter("responseRaw");
		if (sResponse) {
			var oResponse = JSON.parse(sResponse);
			if (oResponse.resultType === 'SUCCESS') {
				this.getView().getModel('vendors').setData(oResponse);
			} else {
				oTarget = this.getView().byId('vendorListPage');
				for (var v = 0; v < oResponse.result.length; v++) {
					var resultType = oResponse.result[v].resultType;
					var result = oResponse.result[v].result;
					if (resultType === "SUCCESS") {
						var oType = "Success";
						AppHelper.showMsgStrip(oTarget, oType, result);
					} else {
						var oType = "Error";
						AppHelper.showMsgStrip(oTarget, oType, result);
					}
				}
			}
		}
		sap.ui.getCore().byId("vendorFileUploader").setValue("");
		this.onVendorCloseDialog();
		AppHelper.hideLoader();
	},
	onProfileShowEvent: function(oEvent) {
		AppHelper.displayProfileDetails(this, oEvent);
	},

	/**
	 * On User search Request
	 * 
	 * @param oEvent
	 */
	onUserSearch: function(oEvent) {
		if (typeof oEvent.getParameter('refreshButtonPressed') === typeof undefined) {
			return false;
		}
		var that = this;
		var sInput = oEvent.getParameter('query').trim();
		AppHelper.showLoader();
		if (AppHelper.isValidEmail(sInput)) {
			var query = Configuration.manageVendorServiceUrl.query + "?email=" + sInput;
			var onErrorCallBack = function(data) {
				AppHelper.hideLoader();
				AppHelper.displayAlertMsg(AppHelper.messageType.error,data.result); 
			};
			var onSuccessCallBack = function(data) {
				var action = that.actions.create;
				var model = that.getModel("vendor")
				model.setDefaultBindingMode("OneWay");
				var result = data.result;
				if (Object.keys(result).length > 0) {
					result["action"] = action;
					model.setData(result);
				} else {
					model.setData({action: action });
				}
				that.byId('email_input').setValue(sInput);
				AppHelper.hideLoader();
			};
			Connection.requestWithCallbackData(query, onErrorCallBack, onSuccessCallBack);

		} else {
			AppHelper.hideLoader();
			AppHelper.displayAlertMsg(AppHelper.messageType.warning,"'" + sInput + "' is an invalid email address");
		}
	},
	/**
	 * Handle system logout
	 * 
	 * @param oEvent
	 */
	onLogoutRequest: function(oEvent) {

		Connection.logout(Configuration.home);
	},
	_fragments: {},

	_getFragment: function(sFragmentName) {
		var oFragment = this._fragments[sFragmentName];
		if (oFragment) {
			return oFragment;
		}
		oFragment = sap.ui.xmlfragment(this.getView().getId(), "view.admin." + sFragmentName, this);
		this.getView().addDependent(oFragment);
		return this._fragments[sFragmentName] = oFragment;
	},

	_showFragment: function(sFragmentName) {
		var oPage = this.getView().byId("vendorDetailPage");
		oPage.removeAllContent();
		oPage.insertContent(this._getFragment(sFragmentName));
	},
	/**
	 * On Press Edit Vendor Details 
	 */
	onPressEditVendor: function(oEvent) {
		this.oVendorData = this.getSelectedObjectFromList();
		// Enhanced on 21st October 2016
        var createData = this.oVendorData;
		// Enhanced on 21st October 2016
		this.oVendorData["action"] = this.actions.edit;
		this.oVendorData["systemAccountStatusUpdated"] = this.oVendorData.systemAccountStatus;
		AppHelper.showLoader();
		var that = this;
		var url = Configuration.manageVendorServiceUrl.queryAdmin + this.oVendorData.vendorID;
		Connection.requestWithCallbackData(url, function(data) {
			AppHelper.hideLoader();
			AppHelper.displayMsgAlert(data.result)

		}, function(data) {
			var vendorData = Formatter.formatVendorAdminData(data,that.oVendorData);
			that.getModel('vendor').setData(vendorData);
//			that.getModel('administrator').setData(data.result);
			that.toggleToSaveButton();
			AppHelper.hideLoader();
			that._showFragment("ManageVendorData");
		});

	},
	toggleToSaveButton: function() {
		this.byId("btn_edit_vendor").setVisible(false);
		this.byId("btn_new_vendor").setVisible(false);
//		this.byId("bulkVendoCreate").setVisible(false);
		this.byId("btn_cancel_vendor").setVisible(true);
		this.byId("btn_save_vendor").setVisible(true);
		this.toggleDeleteButton(false);
	},
	toggleToListViewButton: function() {
		var oSelectedVendor = this.getSelectedObjectFromList();
		var isVisible = Object.keys(oSelectedVendor).length > 3;
		this.byId("btn_edit_vendor").setVisible(isVisible);
		this.toggleDeleteButton(isVisible);
		if (isVisible) {
			this.toggleEmailButton((!oSelectedVendor.accountValidated));
		}
		this.byId("btn_new_vendor").setVisible(true);
//		this.byId("bulkVendoCreate").setVisible(true);
		this.byId("btn_cancel_vendor").setVisible(false);
		this.byId("btn_save_vendor").setVisible(false);
	},
	onCancelVendorModifyEvent: function(oEvent) {
		this.toggleToListViewButton();
		this._showFragment("VendorList");
	},
	onItemSelectEvent: function(oEvent) {
		this.toggleToListViewButton();
	},
	getFragmentTitle: function(sAction) {
		var sDailogTitle = AppHelper.i18n().getProperty("title_pg_create_vendor");
		if (sAction === this.actions.edit) {
			sDailogTitle = AppHelper.i18n().getProperty("title_update_vendor");
		}
		return sDailogTitle;
	},
	/**
	 * Delete Vendor Account
	 * 
	 * @param oEvent
	 */
	onDeleteVendorAccountEvent: function(oEvent) {
		var that = this;
		var oVendor = this.getSelectedObjectFromList();
		var isInActive = oVendor.systemAccountStatus === 'INACTIVE';
		if (isInActive) {
			sap.m.MessageBox.show(AppHelper.i18n().getProperty("msg_delete_vendor"), {
				icon: sap.m.MessageBox.Icon.ERROR,
				title: AppHelper.i18n().getProperty("title_delete_vendor"),
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function(oAction) {
					if (oAction === sap.m.MessageBox.Action.YES) {
						that.deleteVendorAccount(oVendor);
					}
				}
			});
		} else {
			sap.m.MessageBox.show(AppHelper.i18n().getProperty("msg_update_vendor"), {
				icon: sap.m.MessageBox.Icon.INFORMATION,
				title: AppHelper.i18n().getProperty("title_update_vendor"),
				actions: [sap.m.MessageBox.Action.OK]
			});
		}
	},
	/**
	 * 
	 * @param data
	 */
	deleteVendorAccount: function(data) {
		var that = this;
		var url = Configuration.manageVendorServiceUrl.drop;
		Connection.postData(url, data,
			function(response) {
				that.getView().getModel('vendors').setData(response);
				that.toggleToListViewButton();
			},
			function(xhr, textStatus, errorThrown) {
				var result = xhr.responseJSON.result;
				if (typeof result !== "undefined") {
					AppHelper.displayMsgAlert(result);
				} else {
					AppHelper.displayMsgAlert("Error occured");
				}
			});
	},
	/**
	 * Toggle delete button
	 * 
	 * @param isVisible
	 */
	toggleDeleteButton: function(isVisible) {
		this.byId("btn_delete").setVisible(isVisible);
	},
	
	/*
	 * On Press Save Vendor Account Details
	 */
	onPressSaveVendorDetails : function(){
		var vendorData = this.getModel("vendor").getData();
		//Validate the Account Details
		if(SCIMAccount.validateAccountData(vendorData)){
			var that = this;
			
			var url = (vendorData.action === this.actions.create)?  Configuration.manageVendorServiceUrl.create : Configuration.manageVendorServiceUrl.update;
				try {
					var result="Error occured during creation of Vendor";
					var data = this.frameAccountData(vendorData);
					Connection.postData(url, data,
							function(response) {
						if(response.resultType === "SUCCESS"){
							var message = (vendorData.action === that.actions.create)? AppHelper.i18n().getProperty("success_admin_msg") : 
							AppHelper.i18n().getProperty("success_update_msg");
							AppHelper.displayMsgAlert(message + " "+ vendorData.vendorName,"Success");
							that.initializeVendorsModel();
							that.onCancelVendorModifyEvent();
						}else{
							AppHelper.displayMsgAlert(response.result,"Error");
						}
					},
					function(xhr, textStatus, errorThrown) {
						
						if(xhr.hasOwnProperty("responseJSON")){
							result= xhr.responseJSON.result;
						}
						else if(xhr.hasOwnProperty("responseText")){
							result= xhr.responseText;
						}
						AppHelper.displayMsgAlert(result); 
					});
				} catch (e) {
					AppHelper.displayMsgAlert(e.message);
				}
		}
	},
	/**
	 * Frame Vendor Account Data
	 */
	frameAccountData: function(vendorData){
		var accountData = this.parseJsonData(vendorData);
		accountData.userID = accountData.userID + "@" + accountData.vendorCode;
		accountData.sciUserId = (accountData.sciAccountID)? accountData.sciAccountID : "";
		accountData.systemAccountStatus = accountData.systemAccountStatusUpdated;
		if(accountData.action !== this.actions.edit){
			delete(accountData.action);
		}
//		accountData.sciID = (accountData.sciAccountID) ? accountData.sciAccountID : "";
		var properties = ["systemAccountStatusUpdated","systemUserRole","quotaUtilization","isEdit","vendorUserEmail",
			"validateEmailDomain","sciAccountID","activatedBy","createDateTime","accountValidated","createdBy","userName"];
		for(var i=0;i<=properties.length;i++){
			delete(accountData[properties[i]]);
		}
		
		var tempAccounts = accountData.vendorAccounts;
		accountData.vendorAccounts = [];
		
		if(tempAccounts instanceof Array){
			var vElement;
			for (var j = 0; j < tempAccounts.length; j++) {
				vElement = {};
				vElement.vendorID = (tempAccounts[j].vendorID)? tempAccounts[j].vendorID : "";
				vElement.vendorCode = tempAccounts[j].vendorCode;
				vElement.vendorName = tempAccounts[j].vendorName;
				vElement.isVendorAdmin = (tempAccounts[j].isVendorAdmin)? true : false;
				accountData.vendorAccounts.push(vElement);
			}
		}
		return accountData;
	},
	/**
	 * Update VendorAccount
	 * 
	 * @param data
	 */
	updateVendorAccount: function(data) {
		var that = this;
		var url = Configuration.manageVendorServiceUrl.update;
		Connection.postData(url, data,
			function(response) {
				that.getView().getModel('vendors').setData(response);
				if(response.resultType === "SUCCESS"){
					AppHelper.displayMsgAlert("Admin details successfully updated for vendor "+data.vendorName,"Success");
				}
				that.onCancelVendorModifyEvent();
			},
			function(xhr, textStatus, errorThrown) {
				var result = xhr.responseJSON.result;
				if (typeof result !== "undefined") {
					AppHelper.displayMsgAlert(result);
				} else {
					AppHelper.displayMsgAlert("Error occured");
				}

			});
	},
	onExit: function() {
		for (var sPropertyName in this._fragments) {
			if (!this._fragments.hasOwnProperty(sPropertyName)) {
				return;
			}
			this._fragments[sPropertyName].destroy();
			this._fragments[sPropertyName] = null;
		}
		if (this._oPopover) {
			this._oPopover.destroy();
		}
		if (this._oMenuPopOver) {
			this._oMenuPopOver.destroy();
		}
	},
	allocatedQuota: function(vendorCategoryEnum) {
		//JR
		/*
		var oQuotas = this.getView().getModel('quota').getData().result;
		for (var i = 0; i < oQuotas.length; i++) {
			var oQuota = oQuotas[i];
			if (oQuota.vendorCategory === vendorCategoryEnum) {
				return oQuota.quotaAllocated;
			}
		}*/
	},
	onFilterRequest: function(oEvent) {
		var aFilters = [];
		var sQuery = oEvent.getSource().getValue();
		if (sQuery && sQuery.length > 0) {
			//Vendor Name
			var filter = new sap.ui.model.Filter("vendorName", sap.ui.model.FilterOperator.Contains, sQuery);
			aFilters.push(filter);
			//Vendor code Filter
		    filter = new sap.ui.model.Filter("vendorCode", sap.ui.model.FilterOperator.Contains, sQuery);
		    aFilters.push(filter);
		    
		  //Vendor Ema Filter
		   filter = new sap.ui.model.Filter("vendorAdminEmail", sap.ui.model.FilterOperator.Contains, sQuery);
		   aFilters.push(filter);
		    
		    var listFilter = new sap.ui.model.Filter({
		    	filters : aFilters,
		    	and : false
		    });
		    aFilters = [];
		    aFilters.push(listFilter);
		}
		var list = this.getView().byId("vendorList");
		var binding = list.getBinding("items");
		binding.filter(aFilters, "Application");
	},
	handleVendorDelete: function(oEvent) {
		if (!this._oMenuPopOver) {
			this._oMenuPopOver = sap.ui.xmlfragment("view.admin.DeleteAccountPopOver", this);
			this.getView().addDependent(this._oMenuPopOver);
		}
		var oButton = oEvent.getSource();
		jQuery.sap.delayedCall(0, this, function() {
			this._oMenuPopOver.openBy(oButton);
		});
	},
	/**
	 * handle Email Resend
	 */
	handleEmailResend: function(oEvent) {
		var oSelectedVendor = this.getSelectedObjectFromList();
		if (oSelectedVendor.accountValidated) {
			AppHelper.displayAlertMsg(AppHelper.messageType.warning,AppHelper.i18n.getProperty("msg_account_activated"));
			return false;
		} 
		AppHelper.showLoader();
		var onExceptionCallBack = function(sMessage) {
			AppHelper.displayMsgAlert(sMessage);
			AppHelper.hideLoader();
		};
		var onSuccessCallBack = function(data) { 
		    AppHelper.displayAlertMsg(AppHelper.messageType.success,data.result);
			AppHelper.hideLoader();
		};
		setTimeout(function() {
			var url = Configuration.manageVendorServiceUrl.resendEmail+oSelectedVendor.vendorID;
			Connection.requestWithCallbackData(url, onExceptionCallBack, onSuccessCallBack);
		}, 1000);
	},
	/**
	 * Toggle Email Resend button
	 */
	toggleEmailButton: function(isVisible) {
		this.byId("btn_resend_email").setVisible(isVisible);
	},
	/**
	 * Add Vendor Entries on the screen
	 */
	onPressAddVendors: function() {
		var element = {};
		element.vendorCode = "";
		element.vendorName = "";
		element.isCreate = (this.oVendorData["action"] === this.actions.edit)? false : true;
		element.isNewVendor = true;
		element.isDelete = true;
		var vendorModel = this.getModel("vendor");
		var vendorData = vendorModel.getData();
		vendorData.vendorAccounts = (vendorData.vendorAccounts instanceof Array) ? vendorData.vendorAccounts : [];
		vendorData.vendorAccounts.push(element);
		vendorModel.setData(vendorData);
	},
	/**
	 * On Press Delete Vendors from Table 
	 */
	onPressDelete : function(oEvent){
		var selectedModel = oEvent.oSource.oParent.oBindingContexts.vendor;
		var sPath = selectedModel.sPath;
		selectedModel = selectedModel.oModel;
		var vendorDetails = selectedModel.getProperty(sPath);
		var idx = sPath.charAt(sPath.lastIndexOf('/') + 1);
		idx = Number(idx);
		if (idx !== -1) {
			var tmpData = selectedModel.getData();
			tmpData.vendorAccounts.splice(idx, 1);
			if(vendorDetails && vendorDetails.isVendorAdmin){
				tmpData.userName = "";
			}
			selectedModel.setData(tmpData);
		}
	},
	/*
	 * Select any one admin from the table
	 */
	selectAdmin: function(oEvent) {
		var selectedModel = oEvent.oSource.oParent.oBindingContexts.vendor;
		var sPath = selectedModel.sPath;
		selectedModel = selectedModel.oModel;
		var vendorDetails = selectedModel.getProperty(sPath);
		var isValid = true;
		selectedModel.setProperty("/userName", "");
		selectedModel.setProperty("/vendorName");
		selectedModel.setProperty("/vendorCode");
		if (vendorDetails.vendorCode) {
			vendorDetails.vendorCode = vendorDetails.vendorCode.trim();
			vendorDetails.vendorName = (vendorDetails.vendorName) ? vendorDetails.vendorName.trim() : "";
			
//			selectedModel.setProperty("/userName", "admin@" + vendorDetails.vendorCode);
			selectedModel.setProperty("/vendorName",vendorDetails.vendorName);
			selectedModel.setProperty("/vendorCode",vendorDetails.vendorCode);
		} else {
			isValid = false;
			var oTarget = this.getView().byId('vendorListPage');
			AppHelper.showMsgStrip(oTarget, "Error", "Please provide vendor code for the admin");
		}
		//Selection of any Vendor Admin Code
		var vendorList = selectedModel.getProperty("/vendorAccounts");
		Formatter.selectOneItem(sPath, "isVendorAdmin", selectedModel, vendorList, isValid);
		
		var idx = sPath.charAt(sPath.lastIndexOf('/') + 1);
		for (var i = 0; i < vendorList.length; i++) {
			element = vendorList[i];
			element.isVendorAdmin = (Number(idx) === i);
			if(element.isVendorAdmin){
				vendorList.splice(idx,1);
				vendorList.push(element);
				break;
			}
		}
		selectedModel.setProperty("/vendorAccounts",vendorList);
	},
	/**
	 * On Change Vendor Code
	 */
	onChangeVendorCode: function(oEvent) {
		var vendorCodeCtrl = this.getUIControl(oEvent.getSource().getId());
		if (vendorCodeCtrl.getValue()) {
			//Check for Existing Vendor Code in DB
			this.checkForExisting(true, vendorCodeCtrl);  
		}
	},
	/**
	 * On Change Email Address
	 */
	onChangeEmailAddress: function(oEvent) {
		var emailCtrl = this.getUIControl(oEvent.getSource().getId());
		if (emailCtrl.getValue()) {
			//Check for Existing Email ID
			this.checkForExisting(false, emailCtrl);
		}
	},
	/**
	 * Validate against Email and Vendor Code
	 */
	checkForExisting: function(isVendorCode, control) {
		var that = this;
		var value = control.getValue();
		var url = (isVendorCode)? Configuration.manageVendorServiceUrl.queryVendorCode : Configuration.manageVendorServiceUrl.queryEmail;
		url = url + value;
		Connection.requestWithCallbackData(url, function(data) {
			AppHelper.hideLoader();
			AppHelper.displayAlertMsg("ERROR",data.result);
			control.setValue("");
		}, function(data) {
			var message;
			if(data.resultType === "EXISTS"){
				message = (isVendorCode)? "Vendor Code "+value+" already exists in the system, please provide a new one" : "Account with Email ID : "+value+ " already exists in the system";
				control.setValue("");
				AppHelper.displayAlertMsg("ERROR",message);
			}else{
				control.setValue(value.trim());
			}
		});
	},
	/**
	 * On Select Vendor Account Status
	 */
	selectAccountStatus: function(oEvent) {
		var selectedKey = (typeof(oEvent) === "string") ? oEvent : (oEvent) ? oEvent.getParameter('selectedItem').getProperty("key") : "";
		if (selectedKey === "INACTIVE") {
			AppHelper.displayAlertMsg("WARNING",AppHelper.i18n().getProperty("msg_inactivate_vendor"));
		} 
	},
	/**
	 * Get UI Control
	 */
	getUIControl: function(uiControlId) {
		var uiControl = (this.getView().byId(uiControlId)) ? this.getView().byId(uiControlId) :
			sap.ui.getCore().byId(uiControlId);
		return uiControl;
	},
	/**
	 * 
	 * @param value
	 */
	isEnabled: function(value) {
		// var disabled=(typeof value == typeof undefined);
		return true;
	},
	/**
	 * Initialize Vendor model
	 * 
	 * @method initializeVendorsModel
	 */
	initializeVendorsModel: function() {
		var oVendorsModel = this.getModel('vendors');
		oVendorsModel.setDefaultBindingMode("OneWay");
		oVendorsModel.setData(Connection.requestData(Configuration.manageVendorServiceUrl.query));
	},
	/**
	 * @method getSelectedObjectFromList
	 */
	getSelectedObjectFromList: function() {
		var oSelectedObject = {};
		var oSelectedViewItem = this.byId('vendorList').getSelectedItem();
		if (oSelectedViewItem) {
			var contextPath = oSelectedViewItem.getBindingContext('vendors').getPath();
			oSelectedObject = this.getView().getModel('vendors').getProperty(contextPath);
		}
		return oSelectedObject;
	},
	/**
	 * @method getSelectedObjectFromEvent
	 * @path oEvent
	 */
	getSelectedObjectFromEvent: function(oEvent) {
		var sPath = oEvent.getSource().getBindingContext('vendors').getPath();
		return this.getView().getModel('vendors').getProperty(sPath);
	},
	/**
	 * Parse Object
	 */
	parseJsonData: function(data) {
		if (data) {
			data = JSON.parse(JSON.stringify(data));
		}
		return data;
	}
});