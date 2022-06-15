sap.ui.controller("controller.fpuser.OutBoxDocumentManagement", {

	/**
	 * Called when a controller is instantiated and its View controls (if
	 * available) are already created. Can be used to modify the View before it
	 * is displayed, to bind event handlers and do other one-time
	 * initialization.
	 * 
	 * @memberOf controller.fpuser.OutBoxDocumentManagement
	 */
	onInit : function() {
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"outbox");  
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"members");  
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"documentRequest"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"activity");
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"participant"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"users"); 
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this); 
		AppHelper.initProfileDetails(this);
	},
	loadRequiredData:function(){
		AppHelper.showLoader()
		this.getView().getModel('outbox').setData(Connection.requestData(Configuration.fairPriceUserAccountServiceUrl.documentOutBox));
		AppHelper.hideLoader();
		this.defaultButtonVisibility();
	},
	/**
	 * handle routing
	 */
	onRouteMatched : function(oEvent) {
		var sName = oEvent.getParameter('name');
		if (sName === "outbound_share") {
			this.loadRequiredData(); 
			this._showFragment("OutBoxDocumentList");
			this.updateBreadCrumb(true,"documentListPanel");
			this.byId("documentList").removeSelections(true);
		}else{
			console.log("Wrong Path"+sName);
			return;
		}   
	},
	/**
	 * Handle the click events
	 */
	onItemClickEvent : function(oEvent) { 
		this.oDocument=this.getView().getModel('outbox').getProperty(oEvent.getSource().getBindingContext('outbox').sPath); 
		if(this.oDocument.contentType==="doc/folder"){
			this.currentFolder=this.getView().getModel('outbox').getProperty(oEvent.getSource().getBindingContext('outbox').sPath);
			this.loadLocationContent(this.oDocument.openCmisID,this.oDocument.openCmisParentID,this.oDocument.contentType);
		}else{
			this.initDownload(this.oDocument);
		}
		
	},
	isDraft:function(oStatus){
		return (oStatus!=='RELEASED')?"DRAFT":"";
	},
	onItemSelectedEvent:function(oEvent){  
		this.oDocument=this.getView().getModel('outbox').getProperty(oEvent.getSource().getSelectedItem().getBindingContext('outbox').sPath);
		var isFolder=Formatter.isFolder(this.oDocument.contentType);
		this.toggleButton(isFolder);
		if(!isFolder){
			this.toggleUploadButton(true);
		}  
		this.toggleDraftButtons(Formatter.isDraft(this.oDocument.requestStatus));
	},
	
	toggleButton:function(isFolder){ 
	    this.getView().byId('btnDownload').setVisible(!isFolder); 
	    this.getView().byId('btnDetails').setVisible(isFolder);
	    this.getView().byId('btnUpload').setVisible(isFolder);
	    this.toggleAddParticipant(isFolder);
	},
	toggleUploadButton:function(isVisible){
		 this.getView().byId('btnUpload').setVisible(isVisible);  
	},
	toggleInFolderButton:function(){
		this.toggleUploadButton(true);
		this.getView().byId('btnDownload').setVisible(false); 
	    this.getView().byId('btnDetails').setVisible(false); 
	    this.toggleDraftButtons(false);
	    this.toggleAddParticipant(false);
	},
	defaultButtonVisibility:function(){ 
	    this.getView().byId('btnDownload').setVisible(false); 
	    this.getView().byId('btnDetails').setVisible(false);
	    this.getView().byId('btnUpload').setVisible(false); 
	    this.toggleDraftButtons(false);
	    this.toggleAddParticipant(false);
	},
	toggleAddParticipant:function(isVisible){
		this.getView().byId('btnAddParticipant').setVisible(isVisible);
	},
	toggleDetailsButtons:function(){
		this.defaultButtonVisibility();
		this.toggleBackButton(false);
	},
	updateWithContextItems:function(sPath){
		jQuery.sap.require("sap.m.MessageToast");   
		this.data=this.oModel.getProperty(sPath,this.oModel);
		console.log(this.data.contentType); 
		if(this.data.contentType==="doc/folder"){  
			this.getView().getModel().setData(this.data); 
		}  
	},
	/**
	 * Handle the creation of document share
	 * @param oEvent
	 */
	onCreateDocument:function(oEvent){
		try { 
			var oArguments = {"fragmentName": "DocumentShareRequest", args: {action:"CREATE"} }; 
			//navigate 
			this.router.navTo('document_share', {
				arguments : JSON.stringify(oArguments)
			});
		}catch (e) {
			console.log(e.message);
		} 
	},
	 
	
	_fragments : {},
		
	_getFragment : function(sFragmentName) {
		var oFragment = this._fragments[sFragmentName]; 
		if (oFragment) {
			return oFragment;
		} 
		oFragment = sap.ui.xmlfragment(this.getView().getId(),"view.fpuser." + sFragmentName,this);
		this.getView().addDependent(oFragment);
		return this._fragments[sFragmentName] = oFragment;
	},
		
	_showFragment : function(sFragmentName) {
		var oPage = this.getView().byId("documentListPage"); 
		oPage.removeAllContent();
		oPage.insertContent(this._getFragment(sFragmentName));
	}, 
	
	loadLocationContent:function(oOpenCmisDocumentID,oOpenCmisDocumentParent,oContentType){ 
		var that=this;
		var data={documentID:oOpenCmisDocumentID,parentID:oOpenCmisDocumentParent};   
		var url=Configuration.fairPriceUserAccountServiceUrl.documentQuery;
		Connection.postData(url, data, 
					function(response){
							if(response.result.length>0){
								that.getView().getModel('outbox').setData(response);
								var searchField=that.byId("sharedSearchInput");
								searchField.setValue("");
								searchField.fireLiveChange(); 
								that.getView().byId("documentList").removeSelections();
								that.toggleInFolderButton();
								that.updateBreadCrumb(false,"documentListPanel");
							}else{
								sap.m.MessageToast.show("No items found"); 
							}
				             
					}, 
					function(xhr, textStatus, errorThrown){
						if(xhr.responseJSON.resultType!=="SUCCESS"){ 
							 sap.m.MessageToast.show(xhr.responseJSON.result); 
					} 
		}); 
	}, 
	/**
	 * Handle download
	 */
	handleDownload:function(oEvent){
		var sPath=this.byId('documentList').getSelectedItem().getBindingContext("outbox").sPath;
		var oDocument=this.getView().getModel('outbox').getProperty(sPath); 
		this.initDownload(oDocument);
	},
	initDownload:function(oDocument){
		var url=Configuration.fairPriceUserAccountServiceUrl.documentDownload; 
		Connection.dowloadRequest(oDocument, url);
	},
	getModel:function(oModelName){
		var oModel=this.getView().getModel(oModelName);
		if(!oModel){ 
			oModel=new sap.ui.model.json.JSONModel();
			sap.ui.getCore().setModel(oModel,oModelName);
		}
		return oModel;
	},
	deleteDocument:function(onProceedDeletion){
		sap.m.MessageBox.show( AppHelper.i18n().getProperty("msg_delete_document"), {
            icon: sap.m.MessageBox.Icon.ERROR,
            title: AppHelper.i18n().getProperty("title_delete_document"),
            actions: [sap.m.MessageBox.Action.YES,sap.m.MessageBox.Action.NO],
            onClose: function (oAction) { 
            	if(oAction===sap.m.MessageBox.Action.YES){
            		 return onProceedDeletion();
            	} 
            }
		});  
	},
	handleDelete:function(oEvent){
		var oSelectedItem=this.byId('documentList').getSelectedItem();  
		var oDocument=this.getView().getModel("outbox").getProperty(oSelectedItem.getBindingContext('outbox').sPath);   
		var that=this;
		this.deleteDocument(function(){
			var seletedDocumentIDs={collection:[]}; 
			seletedDocumentIDs.collection.push({documentID:oDocument.openCmisID});
    		var url=Configuration.fairPriceUserAccountServiceUrl.documentDelete;
    		Connection.postData(url, seletedDocumentIDs, 
    				function(response){  
    					if(oDocument.contentType==="doc/folder"){
    						that.defaultLoad();
    					}else{
    						that.loadLocationContent(oDocument.openCmisParentID,oDocument.openCmisParentID,oDocument.contentType);
    					}
    				}, 
    				function(xhr, textStatus, errorThrown){ 
    				sap.m.MessageToast.show(xhr.responseJSON.result);  
    		});
		});
		 
	}, 
	toggleDraftButtons:function(isDraft){
		this.getView().byId('btnDelete').setVisible(isDraft);   
		this.getView().byId('btnEdit').setVisible(isDraft);  
	},
	/**
	 * Handle show details
	 */
	handleDetail:function(oEvent){
		try{
			AppHelper.showLoader();
			var that=this;
			var sPath=this.byId('documentList').getSelectedItem().getBindingContext('outbox').sPath;
			var oSelectedDocument=this.getModel('outbox').getProperty(sPath);
			var url=Configuration.fairPriceUserAccountServiceUrl.queryDocumentMembers+oSelectedDocument.documentShareID; 
			Connection.requestWithCallbackData(url, function(data){
				AppHelper.hideLoader();
			}, function(data){ 
				oSelectedDocument["documentSizeMB"]=data.relMap.size; 
				that.getModel('activity').setData({"result":data.relMap.log}); 
				that.getModel('participant').setData({"result":data.relMap.participants});
				that.getModel('documentRequest').setData(oSelectedDocument);
				that.getModel('members').setData(data)
				that._showFragment("OutBoxRequestDetail");
				that.updateBreadCrumb(false, "outboxRequestDetailPanel");
				that.toggleDetailsButtons();
				that.toggleAddParticipant(true);
				AppHelper.hideLoader();
			});
		}catch(e){
			console.log(e);
			AppHelper.hideLoader();
		}
	}, 
	getCreatedBy:function(){
		var profile=AppHelper.profile().getData();	
		return profile.firstName+" "+profile.lastName;
	},
	createdBy:function(oCreatedBy){
		var data=this.getModel('participant').getData().result;
		for(var i in data){
			var oParticipant = data[i];
			var participantType = oParticipant.participantType;
			if(participantType==="ORIGINATOR"){
				var firstName=oParticipant.dSFairPriceUser.firstName;
				var lastName=oParticipant.dSFairPriceUser.lastName;
				return firstName +" "+lastName;
			} 
		}
	},
	onFilterRequest : function(oEvent) {  
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
	getUploadDialog : function () {
		if (!this.oUploadDialog) {
			// create dialog via fragment factory
			this.oUploadDialog = sap.ui.xmlfragment("view.all.UploadDialog", this);
			// connect dialog to view (models, lifecycle)
			this.getView().addDependent(this.oUploadDialog);
		}
		return this.oUploadDialog;
	},
	getParticipantDialog : function () {
		if (!this.oParticipantDialog) {
			// create dialog via fragment factory
			this.oParticipantDialog = sap.ui.xmlfragment("view.fpuser.FairpriceListDialog", this);
			// connect dialog to view (models, lifecycle)
			this.getView().addDependent(this.oParticipantDialog);
		}
		return this.oParticipantDialog;
	}, 
	onCloseDialog : function (oEvent) {
		this.getUploadDialog().close();
	},
	/**
	 * handle participants
	 * @param oEvent
	 */
	handleAddParticipant:function(oEvent){ 
		this.loadAvailableParticipant(this.getSelectedDocument());
		this.getParticipantDialog().open();
	},
	getSelectedDocument:function(){
		var oSelectedItem=this.byId('documentList').getSelectedItem();  
		var oDocument=this.getView().getModel("outbox").getProperty(oSelectedItem.getBindingContext('outbox').sPath);    
		return oDocument;
	},
	/**
	 * load available users
	 */
	loadAvailableParticipant:function(oDocument){
		AppHelper.showLoader()
		this.getView().getModel('users').setData(Connection.requestData(Configuration.fairPriceUserAccountServiceUrl.availableParticipant+oDocument.documentShareID));
		AppHelper.hideLoader();
	},
	/**
	 * onAdd Participant
	 * @param oEvent
	 */
	onAddParticipant:function(oEvent){
		var that=this;
		var data=this.getParticipationData();
		var url=Configuration.fairPriceUserAccountServiceUrl.addParticipant;  
		Connection.postData(url, data, 
				function(response){  
					that.getModel('participant').setData(response);
					that.getParticipantDialog().close();
				}, 
				function(xhr, textStatus, errorThrown){ 
				sap.m.MessageToast.show(xhr.responseJSON.result);  
		}); 
	},
	getParticipationData:function(){
		var selectedItems=sap.ui.getCore().byId('availableParticipants').getSelectedItems();  
		var oParticipationData={collection:[]};
		oParticipationData["documentShareID"]=this.getSelectedDocument().documentShareID;
		for ( var selectedIndex in selectedItems) {
			var oUser=this.getModel('users').getProperty(selectedItems[selectedIndex].getBindingContext('users').sPath); 
			var oParticipant={fairPriceUserID:oUser.fairPriceUserID};
			oParticipationData.collection.push(oParticipant);
		}
		return oParticipationData;
	},
	showParticipationDelete:function(oParticipantType,createdBy ){
		if(oParticipantType==="CONTRIBUTOR"){ 
			return (AppHelper.profile().getData().userName===createdBy); 
		}
		return false; 
	},
	onDropParticipation:function(oEvent){
		var that=this; 
		var data =this.getView().getModel('participant').getProperty(oEvent.getSource().getBindingContext('participant').sPath);
		this.proceedParticipationDrop(function(){ 
			var url=Configuration.fairPriceUserAccountServiceUrl.removeParticipant;  
			Connection.postData(url, data, 
					function(response){  
						that.getModel('participant').setData(response);
						that.getParticipantDialog().close();
					}, 
					function(xhr, textStatus, errorThrown){ 
					sap.m.MessageToast.show(xhr.responseJSON.result);  
			}); 
		});
	},
	proceedParticipationDrop:function(onProceedDeletion){
		sap.m.MessageBox.show( AppHelper.i18n().getProperty("msg_drop_participant"), {
            icon: sap.m.MessageBox.Icon.ERROR,
            title: AppHelper.i18n().getProperty("title_drop_participant"),
            actions: [sap.m.MessageBox.Action.YES,sap.m.MessageBox.Action.NO],
            onClose: function (oAction) { 
            	if(oAction===sap.m.MessageBox.Action.YES){
            		 return onProceedDeletion();
            	} 
            }
		});  
	},
	onCloseUserDialog:function(oEvent){
		this.getParticipantDialog().close();
	},
	/**
	 * @memberOf controller.vuser.DocumentShare
	 * @param oEvent
	 */
	handleUploadPress:function(oEvent){
		AppHelper.showLoader();
		var oFileUploader= sap.ui.getCore().byId("docShareFileUploader");
		var expiryTenure=this.getView().getModel("req").getData().expiryTenure;
		if(expiryTenure<1||expiryTenure>30){
			AppHelper.displayMsgAlert("Availability period for document is limited to 30days!");
			AppHelper.hideLoader();
		}else{
			if(!oFileUploader.getValue()){
				sap.m.MessageToast.show("Please choose file");
				AppHelper.hideLoader();
				return;
			} 
			oFileUploader.upload();
		}
	},
	
	/**
	 * Handle Upload on completion
	 * @memberOf controller.vuser.DocumentShare
	 * @param oEvent
	 */
	handleUploadComplete:function(oEvent){ 
		var sResponse=oEvent.getParameter("responseRaw");
		if(sResponse){ 
			var oResponse=JSON.parse(sResponse);
			if(oResponse.resultType==='SUCCESS'){  
				this.getUploadDialog().close();
				var data=this.getView().getModel("req").getData();
				if(data.showTenureInput){
					this.loadLocationContent(this.currentFolder.openCmisID,this.currentFolder.openCmisParentID,this.currentFolder.contentType);
				}
			}else{
			   sap.m.MessageToast.show(oResponse.result);
			}
			AppHelper.hideLoader(); 
		} 
		sap.ui.getCore().byId("docShareFileUploader").setValue("");
	},
	handleUpload:function(oEvent){
		var oRequestModel=new sap.ui.model.json.JSONModel();
		this.getView().setModel(oRequestModel,"req"); 
		var oSelectedItem=this.byId('documentList').getSelectedItem();
		var data={};
		if(!oSelectedItem){
			var openCmisID=this.currentFolder.openCmisID;
			data={openCmisID:openCmisID,showTenureInput:true};
		}else{
			var oDocument=this.getView().getModel("outbox").getProperty(oSelectedItem.getBindingContext('outbox').sPath);
			if(oDocument.contentType==="doc/folder"){
				var openCmisID=oDocument.openCmisID;
				data={openCmisID:openCmisID,showTenureInput:true}; 
				this.currentFolder=oDocument;
			}else{ 
				var openCmisID=this.currentFolder.openCmisID;
				data={openCmisID:openCmisID,showTenureInput:true};
			}
		} 
		oRequestModel.setData(data); 
		this.getUploadDialog().open(); 
	}
	, 
	/**
	 * Display the user profile
	 * @param oEvent
	 */
	onProfileShowEvent: function (oEvent) { 
		AppHelper.displayProfileDetails(this, oEvent);
	},
	/**
	 * Handle system logout
	 * @param oEvent
	 */
	onLogoutRequest:function(oEvent){ 
		Connection.logout(Configuration.home); 
	},
	isPasscodeSecured:function(oRestrictions){
		if(!oRestrictions){
			oRestrictions=this.oDocument.restriction||{};
		}
		return oRestrictions.hasOwnProperty("PassCodeSecure");
	},
	isWriteAllowed:function(oRestrictions){
		if(!oRestrictions){
			oRestrictions=this.oDocument.restriction||{};
		}
		return oRestrictions.hasOwnProperty("AllowReadAndWrite");
	},
	isLimitedAvailability:function(oRestrictions){
		if(!oRestrictions){
			oRestrictions=this.oDocument.restriction||{};
		}
		return oRestrictions.hasOwnProperty("LimitAvailability");
	},
	isDownloadable:function(oRestrictions){
		if(!oRestrictions){
			oRestrictions=this.oDocument.restriction||{};
		}
		return oRestrictions.hasOwnProperty("AllowDownload");
	},  
	 /* 
	 * @param oCount
	 */
	formatCount:function(oCount){
		if(oCount===0){
			return "";
		}
		return oCount;
	}, 
	 /* 
	 * @param oCount
	 */
	arrayCount:function(value){
		if (value) {
			return value.length;
		} else {
			return 0;
		}
	}, 
	
	restrictionCount:function(oRestrictions){
		var count=0;
		if(oRestrictions){
			count=Object.keys(oRestrictions).length;
		}
		return count;
	},
	handleEdit:function(oEvent){
		var oSelectedItem=this.byId('documentList').getSelectedItem();  
		var oDocument=this.getView().getModel("outbox").getProperty(oSelectedItem.getBindingContext('outbox').sPath);  
		try { 
			var oArguments = {"fragmentName": "DocumentShareRequest", args: {action:"UPDATE",documentShareID:oDocument.documentShareID} }; 
			//navigate 
			this.router.navTo('document_share', {
				arguments : JSON.stringify(oArguments)
			});
		}catch (e) {
			console.log(e.message);
		} 
	},
	/**
	 * 
	 */
	updateBreadCrumb:function(isBase,targetLocationID){ 
		var sInboxHeader=AppHelper.i18n().getProperty("hdr_outbox_doc");
		if(!isBase){
			var sDocumentName=this.oDocument.documentName; 
			var oRootLabel=new sap.m.Label({text:sInboxHeader,design:"Bold"});
			var pathIcon=new sap.ui.core.Icon({src:"sap-icon://slim-arrow-right"});
			var infoToolBar=new sap.m.OverflowToolbar({design:"Solid",content:[oRootLabel,pathIcon,new sap.m.Label({text:sDocumentName,design:"Bold"})]}); 
			this.byId(targetLocationID).setInfoToolbar(infoToolBar );
			this.toggleBackButton(true);
		}else{ 
			var oRootLabel=new sap.m.Label({text:sInboxHeader,design:"Bold"});
			var infoToolBar=new sap.m.OverflowToolbar({design:"Solid",content:[oRootLabel]}); 
			this.byId(targetLocationID).setInfoToolbar(infoToolBar );
			this.toggleBackButton(false);
		} 
	},
	onPressBackButton:function(oEvent){
		this._showFragment("OutBoxDocumentList");
		this.updateBreadCrumb(true,"documentListPanel");
		this.defaultButtonVisibility();
		this.byId("documentList").removeSelections(true);
	},
	/**
	 * 
	 * @param oEvent
	 */
	onBackPress:function(oEvent){
		this.defaultLoad();
	},
	toggleBackButton:function(isFolder){ 
	    this.getView().byId('shared_docs_back').setVisible(isFolder);  
	},
	onProfileClosePress:function(oEvent){
		AppHelper.closeProfileDetails(this);
	}, 			
	defaultLoad : function() {
		this.updateBreadCrumb(true, "documentListPanel");
		this.loadRequiredData();
		this.defaultButtonVisibility();
		this.byId("documentList").removeSelections(true);
	}
	
});