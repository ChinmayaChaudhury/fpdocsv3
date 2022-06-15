sap.ui.controller("controller.fpuser.DocumentShare", {

	/**
	 * Called when a controller is instantiated and its View controls (if available) are already created.
	 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
	 * @memberOf controller.fpuser.DocumentShare
	 */
	onInit : function() {  
		this.i18n = sap.ui.getCore().getModel('i18n'); 
		
		this.getView().setModel(new sap.ui.model.json.JSONModel() ,"dst");
		this.getView().setModel(new sap.ui.model.json.JSONModel() ,"emls"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel() ,"vds"); 
		
		this.oModel = sap.ui.getCore().getModel("emails");
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"col");  
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"uploaded");
		
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"req");
		  
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"participants"); 
		 
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this);
		AppHelper.initProfileDetails(this);
	}, 
	
	/**
	 * Load the vendorGroup Emails
	 * @param vendorID
	 * @param vendorGroupID
	 */
	loadEmailsInGroup:function(vendorID,vendorGroupID){
		AppHelper.showLoader();
		var that=this;
		var data={vendorID:vendorID,vendorGroupID:vendorGroupID}; 
		Connection.postData(Configuration.fairPriceUserAccountServiceUrl.queryByVendorIDnVendorGroupID, data, function(responseData){
			 that.getView().getModel('emls').setData(responseData);
		}, function(xhr, textStatus, errorThrown) {}); 
		AppHelper.hideLoader();
	},

	loadVendorGroups:function(){
		AppHelper.showLoader();
		var requestData=Connection.requestData(Configuration.fairPriceUserAccountServiceUrl.queryAvailableVendorGroups);
	    this.getView().getModel('dst').setData(requestData);
		AppHelper.hideLoader();
	},
	loadParticipants:function(vendorID){
		AppHelper.showLoader();
		var requestData=Connection.requestData(Configuration.fairPriceUserAccountServiceUrl.availableContributors+vendorID);
	    this.getView().getModel('participants').setData(requestData);
		AppHelper.hideLoader();
	},
	loadAvailableVendors:function(){
		AppHelper.showLoader();
		var requestData=Connection.requestData(Configuration.fairPriceUserAccountServiceUrl.queryAvailableVendor);
	    this.getView().getModel('vds').setData(requestData);
		AppHelper.hideLoader();
	},
	onDeleteDocumentsFromList:function(oEvent){
		this.oDocument=this.getView().getModel('uploaded').getProperty(oEvent.getParameter("listItem").getBindingContext('uploaded').sPath);
		var that=this;
		this.deleteDocument(function(){
			var seletedDocumentIDs={collection:[]}; 
			seletedDocumentIDs.collection.push({documentID:that.oDocument.openCmisID});
    		var url=Configuration.fairPriceUserAccountServiceUrl.documentDelete;
    		Connection.postData(url, seletedDocumentIDs, 
    				function(response){  
    					that.removeDocumentFromObjectArray(that.oDocument);
    				}, 
    				function(xhr, textStatus, errorThrown){ 
    				sap.m.MessageToast.show(xhr.responseJSON.result);  
    		});
		});	
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
	isEdit:function(){
		return this.oDSData.getData().hasOwnProperty("documentShareRequestID");
	},
	/**
	 * 
	 * @param oEvent
	 */
	onPressBackButton:function(oEvent){
		try {   
			var oArguments = {"fragmentName": "DocumentShareRequest", args: {action:this.actions.update} }; 
			this.router.navTo('document_share', { arguments : JSON.stringify(oArguments)}); 
		}catch (e) {
			console.log(e.message);
		} 
	},
	actions:{create:"CREATE",update:"UPDATE"},
	/**
	 * Handle routing
	 * 
	 * @param oEvent
	 */
	onRouteMatched : function(oEvent) {
		if(oEvent.getParameter("name")!=="document_share"){
			return;
		}
		this.oArguments =  JSON.parse(oEvent.getParameter("arguments").arguments||{});
		var sFragmentName = this.oArguments.fragmentName;
		var args=this.oArguments.args;
		switch (sFragmentName) {
			case "DocumentShareRequest": 
				this._toggleButtonsAndView(true,sFragmentName); 
				if(args.action===this.actions.create){
					this.getView().getModel("req").setData({});
				} 
				this.loadVendorGroups();
				this.loadAvailableVendors();
				var documentShareID=args.documentShareID;
				if((args.action==this.actions.update)&&documentShareID){
					this.loadExistingDocuments(documentShareID);
				}
				break;
			case "DocumentShareLocation": 
				this._toggleButtonsAndView(false,sFragmentName);
				this.getView().setModel(this.oDSData,"req");
				break;   
			default:
				return;

		}  
	},
	
	loadExistingDocuments:function(oDocumentShareID){ 
		AppHelper.showLoader();
		var requestData=Connection.requestData(Configuration.fairPriceUserAccountServiceUrl.editServiceRequest+oDocumentShareID);
		var oDocumentRequest = requestData.result;
		this.oDSData=this.getView().getModel("req");
		this.oDSData.setData(oDocumentRequest); 
		var uploads={collection:requestData.relMap.uploads.result};
		this.getView().getModel('uploaded').setData(uploads);
		if(oDocumentRequest.availabilityLimited){
			this.byId('availability_input').setVisible(true); 
		}
		if(oDocumentRequest.passcodeSecured){
			this.byId("passcode_input_01").setVisible(true);
			this.byId("passcode_input_00").setVisible(true);
		}
		this.loadEmailsInGroup(oDocumentRequest.vendorID,oDocumentRequest.vendorGroupID);
		this.loadParticipants(oDocumentRequest.vendorID);
		AppHelper.hideLoader();
	},
	/**
	 * Invoked on back navigations
	 * 
	 * @param oEvent
	 */
	onNavBack:function(oEvent){
		try {  
			this.resetDocumentShare(this);
			this.getView().getModel("uploaded").setData({collection:[]});
			this.router.navTo("outbound_share", { index: 2 }, false); 
		}catch (e) {
			console.log(e.message);
		} 
	},
	resetDocumentShare:function(){  
		this.byId('document_share_input').setValueState(sap.ui.core.ValueState.None);  
		this.byId('document_share_reason_input').setValueState(sap.ui.core.ValueState.None);  
		this.byId('vendorCombo').setValueState(sap.ui.core.ValueState.None);  
		this.byId('shareTargetGroup').setValueState(sap.ui.core.ValueState.None); 
		this.byId('multiEmailCombo').setValueState(sap.ui.core.ValueState.None); 
		
		this.byId('passcode_switch').setState(false);
		this.byId('switch_availability').setState(false);
		
		var iLimitedAvailability=this.byId('availability_input');
		var iConfirmPasscode=this.byId("passcode_input_01");
		var iPasscode=this.byId("passcode_input_00");
		iPasscode.setValueState(sap.ui.core.ValueState.None); 
		iPasscode.setVisible(false);
		
		iConfirmPasscode.setValueState(sap.ui.core.ValueState.None);
		iConfirmPasscode.setVisible(false);  
		
		iLimitedAvailability.setValueState(sap.ui.core.ValueState.None); 
		iLimitedAvailability.setVisible(false);   
	},
	/**
	 * Handle selection change events on the email combo
	 * 
	 * @param oEvent
	 */
	handleSelectionChange : function(oEvent) {

	},
	/**
	 * Handle selection finish events on the email combo
	 * 
	 * @param oEvent
	 */
	handleSelectionFinish : function(oEvent) {

	},

	/**
	 * Handle the change events on the switch
	 * 
	 * @method handleSwitch
	 */

	handleSwitchPasscode : function(oEvent) {
		var isControlEnabled = oEvent.getSource().getState();
		var byId=this.getView().byId("passcode_input_00");
		byId.setVisible(isControlEnabled);
		byId.setValue("");
		var byId2=this.getView().byId("passcode_input_01");
		byId2.setVisible(isControlEnabled);
		byId2.setValue("");
	},
	/**
	 * Handle the change events on the switch
	 * 
	 * @method handleSwitch
	 */
	handleSwitchAvailability : function(oEvent) {
		var isControlEnabled = oEvent.getSource().getState();
		var byId=this.getView().byId("availability_input");
		byId.setVisible(isControlEnabled);
		byId.setValue("");
	},
	/**
	 * Handle the document on save
	 * @param oEvent
	 */
	onSaveDocumentShare : function(oEvent) {  
		var isValid=true;
		var sShareRequestName = this.byId('document_share_input').getValue().trim();
		if(sShareRequestName===""){
			this.byId('document_share_input').setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			this.byId('document_share_input').setValueState(sap.ui.core.ValueState.None);
		} 
		var sShareReason = this.byId('document_share_reason_input').getValue().trim();
		if(sShareReason===""){
			this.byId('document_share_reason_input').setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			this.byId('document_share_reason_input').setValueState(sap.ui.core.ValueState.None);
		}  
		
		var sVendor = this.byId('vendorCombo').mProperties.selectedKey.trim();
		if(sVendor.length==0){
			this.byId('vendorCombo').setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			this.byId('vendorCombo').setValueState(sap.ui.core.ValueState.None);
		} 
		
		var sTargetGroup = this.byId('shareTargetGroup').mProperties.selectedKey.trim();
		if(sTargetGroup.length==0){
			this.byId('shareTargetGroup').setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			this.byId('shareTargetGroup').setValueState(sap.ui.core.ValueState.None);
		}  
		var aSharedWithEmails = this.byId('multiEmailCombo').getSelectedKeys();
		if(aSharedWithEmails.length==0){
			this.byId('multiEmailCombo').setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			this.byId('multiEmailCombo').setValueState(sap.ui.core.ValueState.None);
		}  
		var isPassCodeSecured = this.byId('passcode_switch').getState();
		if(isPassCodeSecured){
			var passCode=this.byId("passcode_input_00").getValue().trim();
			var passCodeConfirm=this.byId("passcode_input_01").getValue().trim();
			if(passCode===""||passCodeConfirm===""){
				this.byId('passcode_input_00').setValueState(sap.ui.core.ValueState.Error);
				this.byId('passcode_input_01').setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}
			else if(passCode===passCodeConfirm){  
				this.byId('passcode_input_00').setValueState(sap.ui.core.ValueState.None);
				this.byId('passcode_input_01').setValueState(sap.ui.core.ValueState.None); 
			}else{
				this.byId('passcode_input_00').setValueState(sap.ui.core.ValueState.Error);
				this.byId('passcode_input_01').setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}
		}   
		var isAvailabilityLimited = this.byId('switch_availability').getState(); 
		if(isAvailabilityLimited){
			var availabilityDays=this.byId("availability_input").getValue().trim();
			if(availabilityDays.length===0){
				this.byId('availability_input').setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}
			else if(availabilityDays<1){
				this.byId('availability_input').setValueState(sap.ui.core.ValueState.Error);
				isValid=false;
			}
			else if(availabilityDays>30){
				this.byId('availability_input').setValueState(sap.ui.core.ValueState.Error);
				this.byId('availability_input').setValueStateText(AppHelper.i18n().getProperty('msg_availability_validation'));
				isValid=false;
			}
			else{
				this.byId('availability_input').setValueState(sap.ui.core.ValueState.None); 
			} 
		} 
		 
		if(!isValid){ 
			sap.m.MessageBox.show( AppHelper.i18n().getProperty("validation_error_msg"), {
	                  icon: sap.m.MessageBox.Icon.ERROR,
	                  title: AppHelper.i18n().getProperty("validation_error_header"),
	                  actions: [sap.m.MessageBox.Action.OK],
	                  onClose: function (oAction) { 
	                  }
	       }); 
		}else{ 
			this.saveDestinationDetails(this.getView().getModel("req").getData());  
		} 
	},
	defaultShareTenure:function(oShareDocument){
		if(oShareDocument.availabilityTenure===""){
			oShareDocument.availabilityTenure=30;
		} 
	},
	updateDestinationDetails:function(){
		var oShareDocument=this.getView().getModel('req').getData();
		this.defaultShareTenure(oShareDocument);
		var that=this; 
		Connection.postData(Configuration.fairPriceUserAccountServiceUrl.updateServiceRequest, oShareDocument, 
				function(response){ 
					try {  
						that.oDSData.setData(response);
						var oArguments = {"fragmentName": "DocumentShareLocation", args: {} };
						//navigate 
						that.router.navTo('document_share', {
							arguments : JSON.stringify(oArguments)
						}); 
					}catch (e) {
						console.log(e.message);
					}    
				}, 
				function(xhr, textStatus, errorThrown){  
					sap.m.MessageBox.show( xhr.jsonResponse.result, {
		                  icon: sap.m.MessageBox.Icon.ERROR,
		                  title: AppHelper.i18n().getProperty("validation_error_header"),
		                  actions: [sap.m.MessageBox.Action.OK],
		                  onClose: function (oAction) { 
		                  } 
					});
			}); 
	},
	/**
	 * Create the destination directory
	 * @param oDSData
	 */
	saveDestinationDetails:function(oShareDocument){
		this.defaultShareTenure(oShareDocument);
		if(!this.oDSData){
			this.oDSData=new  sap.ui.model.json.JSONModel();
		}
		if(this.isEdit()){
			this.updateDestinationDetails();
		}else{
			var that=this; 
			this.oDSData.setData(oShareDocument); 
			Connection.postData(Configuration.fairPriceUserAccountServiceUrl.createServiceRequest, oShareDocument, 
					function(response){ 
						try {  
							that.oDSData.setData(response);
							var oArguments = {"fragmentName": "DocumentShareLocation", args: {} };
							//navigate 
							that.router.navTo('document_share', {
								arguments : JSON.stringify(oArguments)
							}); 
						}catch (e) {
							console.log(e.message);
						}    
					}, 
					function(xhr, textStatus, errorThrown){  
						sap.m.MessageBox.show( xhr.jsonResponse.result, {
			                  icon: sap.m.MessageBox.Icon.ERROR,
			                  title: AppHelper.i18n().getProperty("validation_error_header"),
			                  actions: [sap.m.MessageBox.Action.OK],
			                  onClose: function (oAction) { 
			                  } 
						});
				});
		}
	},
	_formFragments : {},

	_toggleButtonsAndView : function(isRequest,sFragmentName) {
		var oView = this.getView();

		// Show the appropriate action buttons
		oView.byId("btnDocumentShareID").setVisible(!isRequest);
		oView.byId("upload_document").setVisible(!isRequest);
		oView.byId("btnCancel").setVisible(isRequest);
		oView.byId("btnSave").setVisible(isRequest);

		// Set the right form type
		this._showFormFragment(sFragmentName);
	},

	_getFormFragment : function(sFragmentName) {
		var oFormFragment = this._formFragments[sFragmentName];

		if (oFormFragment) {
			return oFormFragment;
		}
        //Instantiate and implement a callback by adding dependency
		oFormFragment = sap.ui.xmlfragment(this.getView().getId(), 	"view.fpuser." + sFragmentName,this);
		this.getView().addDependent(oFormFragment);
		return this._formFragments[sFragmentName] = oFormFragment;
	},

	_showFormFragment : function(sFragmentName) {
		var oPage = this.getView().byId("documentSharePage");

		oPage.removeAllContent();
		oPage.insertContent(this._getFormFragment(sFragmentName));
	},
	/**
	 * Handle vendor selects
	 * 1. Populates the group list with  if found
	 * @param oEvent
	 */
	onVendorSelectionChange:function(oEvent){
		this._resetEmailMultiSelectCombo(); 
		var vendorID = this.byId('vendorCombo').mProperties.selectedKey.trim();
		if(vendorID===""){
			this._resetParticipant();	
		}else{
			this.loadParticipants(vendorID);
		}
	},
	onGroupSelectionChange:function(oEvent){
		this._resetEmailMultiSelectCombo();
		var vendorID = this.byId('vendorCombo').mProperties.selectedKey.trim();
		var vendorGroupID = this.byId('shareTargetGroup').mProperties.selectedKey.trim();  
		if(vendorID&&vendorGroupID){
			this.loadEmailsInGroup(vendorID, vendorGroupID);
		} 
		
	}, 
	_resetEmailMultiSelectCombo:function(){
		var data={};
		this.byId("multiEmailCombo").setSelectedKeys(null);
		this.byId("multiEmailCombo").setSelectedItems(null); 
		this.getView().getModel("emls").setData(data);
	},
	_resetParticipant:function(){
		var data={};
		this.byId("multiParticipants").setSelectedKeys(null);
		this.byId("multiParticipants").setSelectedItems(null); 
		this.getView().getModel("participants").setData(data);
	},

	/**
	 * Create the destination directory
	 * @param oDSData
	 */
	createDestinationDirectory:function(oShareDocument){   
		try { 
			var that=this;
			if(!this.oDSData){
				this.oDSData=new  sap.ui.model.json.JSONModel();
			}
			this.oDSData.setData(oShareDocument);  
			Connection.postData(Configuration.fairPriceUserAccountServiceUrl.createServiceRequest, oShareDocument, 
					function(response){ 
						try {  
							that.oDSData.setData(response);
							var oArguments = {"fragmentName": "DocumentShareLocation", args: {} };
							//navigate 
							that.router.navTo('document_share', {
								arguments : JSON.stringify(oArguments)
							}); 
						}catch (e) {
							console.log(e.message);
						}    
					}, 
					function(xhr, textStatus, errorThrown){  
						sap.m.MessageBox.show( xhr.responseJSON.result, {
			                  icon: sap.m.MessageBox.Icon.ERROR,
			                  title: AppHelper.i18n().getProperty("validation_error_header"),
			                  actions: [sap.m.MessageBox.Action.OK],
			                  onClose: function (oAction) { 
			                  } 
					});
			});
			 
		}catch (e) {
			console.log(e.message);
		} 
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
	
	onUploadFile : function (oEvent) {
		var oModel=this.getView().getModel("req");
		var expiryTenure = oModel.getData().availabilityTenure; 
		var data=oModel.getData();
		data["expiryTenure"]=expiryTenure;
		data["showTenureInput"]=false;
		oModel.setData(data);
		this.getUploadDialog().open();
	},

	onCloseDialog : function (oEvent) {
		this.getUploadDialog().close();
	},
	/**
	 * @memberOf controller.vuser.DocumentShare
	 * @param oEvent
	 */
	handleUploadPress:function(oEvent){
		AppHelper.showLoader();
		var oFileUploader= sap.ui.getCore().byId("docShareFileUploader");
		if(!oFileUploader.getValue()){
			sap.m.MessageToast.show("Please choose file");
			AppHelper.hideLoader();
			return;
		} 
		oFileUploader.upload(); 
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
				this.addDocumentObjectToArray(oResponse.result); 
				this.getUploadDialog().close();
			}else{
			   sap.m.MessageToast.show(oResponse.result);
			}
		} 
		AppHelper.hideLoader();
		sap.ui.getCore().byId("docShareFileUploader").setValue("");
	},
	/**
	 * 
	 * @param oDocument
	 */
	addDocumentObjectToArray:function(oDocument){
		var currentData=this.getView().getModel("uploaded").getData(); 
		if(Object.keys(currentData).length===0){
			currentData={collection:[]};
		} 
		currentData.collection.push(oDocument); 
		this.getView().getModel("uploaded").setData(currentData);
		
	},
	/**
	 * 
	 * @param oDocument
	 */
	removeDocumentFromObjectArray:function(oDocument){
		var currentData=this.getView().getModel("uploaded").getData(); 
		var updatedData={collection:[]};
		for(var i in currentData.collection){
			var currentDocument=currentData.collection[i];
			if(currentDocument.openCmisID!==oDocument.openCmisID){
				updatedData.collection.push(currentDocument); 
			} 
		} 
		this.getView().getModel("uploaded").setData(updatedData);
		
	},
	/**
	 * 
	 * @returns
	 */
	getDocumentShareSummaryDialog : function () {
		if (!this.oDocumentShareSummaryDialog) {
			// create dialog via fragment factory
			this.oDocumentShareSummaryDialog = sap.ui.xmlfragment("view.all.DocumentShareSummaryDialog", this);
			// connect dialog to view (models, lifecycle)
			this.getView().addDependent(this.oDocumentShareSummaryDialog);
		}
		return this.oDocumentShareSummaryDialog;
	},
	/**
	 * Handle share request
	 */
	handleShareRequest:function(oEvent){
		this.getDocumentShareSummaryDialog().open(); 
	},
	 
	/**
	 * 
	 */
	closeDialogSummary:function(oEvent){
		this.getDocumentShareSummaryDialog().close(); 
	},
	/**
	 * Submit confirmed documents
	 * @param oEvent
	 */
	submitConfirmedDocumentRequest:function(oEvent){
		var currentData=this.getView().getModel("uploaded").getData(); 
		if(Object.keys(currentData).length===0||Object.keys(currentData.collection).length===0){
			AppHelper.displayMsgAlert("No Uploads found");
		}else{
			var that=this;
			var oShareDocument=this.oDSData.getData();
			Connection.postData(Configuration.fairPriceUserAccountServiceUrl.releaseServiceRequest, oShareDocument, 
					function(response){ 
					       if(response.resultType==="SUCCESS"){
							  that.returnToDocumentList();
							}else{
								sap.m.MessageToast.show(response.result);
							} 
					}, 
					function(xhr, textStatus, errorThrown){  
						AppHelper.displayMsgAlert(xhr.responseJSON.result);
			});
		} 
	} , 
	returnToDocumentList:function(){
		AppHelper.showLoader();
		this.closeDialogSummary(); 
		var bReplace = jQuery.device.is.phone ? false : true;   
        this.router.navTo("outbound_share", { index: 2 },bReplace);
        this.getView().getModel("uploaded").setData({collection:[]});
        AppHelper.hideLoader();
	},
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
	onProfileClosePress:function(oEvent){
		AppHelper.closeProfileDetails(this);
	}
});