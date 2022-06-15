sap.ui.controller("controller.vadmin.ManageAccount", {
	/**
	 * @memberOf controller.vadmin.ManageAccount
	 */
	onInit : function() { 
		this.oInitialLoadFinishedDeferred = jQuery.Deferred();  
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"vu"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"va");
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"vac"); 
		this.getView().setModel(new sap.ui.model.json.JSONModel(),"accounts");  
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this);
		if (sap.ui.Device.system.phone) {
			// Do not wait for the master when in mobile phone resolution
			this.oInitialLoadFinishedDeferred.resolve();
		}
		AppHelper.initProfileDetails(this);
	},
	/**
	*Vendor account actions
	*/
	accountActions:{edit:"edit",display:"display",list:"list"},
	/**
	 * @memberOf controller.vadmin.ManageAccount
	 */
	onRouteMatched : function(oEvent) {
		var sName = oEvent.getParameter("name");
		if (sName === 'admin_account') {
			if(!this.currentAction){
				this.currentAction=this.accountActions.list; 
			}
			switch(this.currentAction){
				case this.accountActions.edit: 
					this._showFormFragment("UpdateVendorAccount"); 
				break;
				case this.accountActions.display: 
					this._showFormFragment("DisplayVendorAccount"); 
				break;
				case this.accountActions.list:
					this.loadVendorList();
					this._showFormFragment("VendorAccountList"); 
				break;
		  }   
		}  
		this.toggleButtonVisibility("viewAccount");
	},
	/**
	 * 
	 * @param oEvent
	 */
	accountDrillDownEvent:function(oEvent){ 
		var sPath=oEvent.getSource().getBindingContext('accounts').sPath;
		var data=this.getView().getModel('accounts').getProperty(sPath);
		this.getView().getModel('va').setData(data);
		this._showFormFragment("DisplayVendorAccount");
		this.currentAction=this.accountActions.display;  
		this.toggleButtonVisibility("viewAccount"); 
	},
	/**
	 * Folder zoom in
	 * 
	 * @memberOf controller.vadmin.ManageAccount
	 */
	onZoomIn : function() {
		var pFolderStructure = this.getView().byId("folderStructure");
		pFolderStructure.zoomIn();
		pFolderStructure.getZoomLevel(); 
	},
	 

	loadVendorUserGoupList :function(){ 
		var requestData=Connection.requestData(Configuration.manageVendorAccountServiceUrl.queryVendorUserGroups);
		this.getView().getModel('vu').setData(requestData);  
	},
	
	loadVendorList:function(){ 
		var requestData=Connection.requestData(Configuration.manageVendorAccountServiceUrl.queryVendorAccount);
	    this.getView().getModel('vac').setData(requestData.keySet);
	    this.getView().getModel('accounts').setData(requestData);
	},
	/**
	 * Folder zoom out
	 * 
	 * @memberOf controller.vadmin.ManageAccount
	 */
	onZoomOut : function() {
		var pFolderStructure = this.getView().byId("folderStructure");
		pFolderStructure.zoomOut();
		pFolderStructure.getZoomLevel(); 
	},
	/**
	 * Node click indicator
	 * 
	 * @memberOf controller.vadmin.ManageAccount
	 */
	onNodePress : function(oEvent) {
		jQuery.sap.require("sap.m.MessageToast");
		var textToDisplay =  oEvent.getParameters().getTitle();
		textToDisplay += " was clicked";
		sap.m.MessageToast.show(textToDisplay);
	},
	/**
	 * Check on tab changes
	 * @param oEvent
	 */
	onTabFocus:function(oEvent){
		jQuery.sap.require("sap.m.MessageToast"); 
		var sSelectedTab= oEvent.getParameter("selectedItem").getAggregation("customData")[0].getProperty("value"); 
		switch(sSelectedTab){
		case "accountTab": 
			this.toggleButtonVisibility("viewAccount"); 
			break;
		case "groupsTab": 
			this.loadVendorUserGoupList();
			this.toggleButtonVisibility("hideAll"); 
			var length = this.getView().byId("vendorGroupList").getSelectedItems().length;
			this.toggleAccountButtonsBasedOnSize(length);
			break;
			 
		}  
	},
	
	toggleButtonVisibility:function(sAction){
		switch(sAction){
			case "viewAccount":
				switch(this.currentAction){
					case this.accountActions.edit:
						this.getView().byId('editAccountDetails').setVisible(false);
						this.getView().byId('cancelAccountDetails').setVisible(true);
						this.getView().byId('viewUsersInGroup').setVisible(false); 
						this.getView().byId('saveAccountDetails').setVisible(true);
					break;
					case this.accountActions.display:
						this.getView().byId('editAccountDetails').setVisible(true);
						this.getView().byId('cancelAccountDetails').setVisible(true);
						this.getView().byId('viewUsersInGroup').setVisible(false); 
						this.getView().byId('saveAccountDetails').setVisible(false);
					break;
					case this.accountActions.list:
						this.hideAllButtons();
					break;
				} 
			break;
			case "manageGroups":
				this.getView().byId('viewUsersInGroup').setVisible(true); 
				this.getView().byId('editAccountDetails').setVisible(false);
				this.getView().byId('saveAccountDetails').setVisible(false);
				this.getView().byId('cancelAccountDetails').setVisible(false);
			break;
			case "updateAccount":
				this.getView().byId('viewUsersInGroup').setVisible(false); 
				this.getView().byId('editAccountDetails').setVisible(false);
				this.getView().byId('saveAccountDetails').setVisible(true);
				this.getView().byId('cancelAccountDetails').setVisible(true);
			break;
			case "hideAll":
				this.hideAllButtons(); 
			break;
		} 
	},
	hideAllButtons:function(){
		 this.getView().byId('viewUsersInGroup').setVisible(false); 
		 this.getView().byId('editAccountDetails').setVisible(false);
		 this.getView().byId('saveAccountDetails').setVisible(false);
		 this.getView().byId('cancelAccountDetails').setVisible(false);
	},
	onCancelAccountUpdate:function(oEvent){
		this._showFormFragment('VendorAccountList');
		this.toggleButtonVisibility('hideAll');
		this.currentAction=this.accountActions.list;
	},
	onEditAccountDetails:function(oEvent){
		this._showFormFragment("UpdateVendorAccount"); 
		this.toggleButtonVisibility("updateAccount");
		this.currentAction=this.accountActions.edit;
	},
	onSaveAccountDetails:function(oEvent){
		//this._showFormFragment("UpdateVendorAccount");
		var that=this;  
		var oVendorAccount=this.getView().getModel('va').getData();  
		var isValid=true; 
		var vendorNameInput=this.byId("vendor_name_input");
		var vendorName=vendorNameInput.getValue();
		if(vendorName===""){ 
			vendorNameInput.setValueState(sap.ui.core.ValueState.Error);
			isValid=false;
		}else{
			vendorNameInput.setValueState(sap.ui.core.ValueState.None);
			oVendorAccount[vendorNameInput.getName()]=vendorName;
		}
		var isEmailValidationEnabledSwitch=this.byId("verify_account_email_switch");
		var isEmailValidationEnabled=isEmailValidationEnabledSwitch.getState();
		if(isEmailValidationEnabled){
			var emailDomainInput=this.byId("email_domain");
			var emailDomain=emailDomainInput.getValue();
			if(emailDomain===""){
				isValid=false;
				emailDomainInput.setValueState(sap.ui.core.ValueState.Error);
			}else{
				if(AppHelper.isDomainValid(emailDomain)){
				emailDomainInput.setValueState(sap.ui.core.ValueState.None);
				oVendorAccount[emailDomainInput.getName()]=emailDomain;
				oVendorAccount[isEmailValidationEnabledSwitch.getName()]=isEmailValidationEnabled;
				}else{
					isValid=false;
					emailDomainInput.setValueState(sap.ui.core.ValueState.Error);
				}
			}
		}
		if(isValid){
			Connection.postData(Configuration.manageVendorAccountServiceUrl.updateVendorAccount, oVendorAccount, 
					function(response){
						that.getView().getModel('va').setData(oVendorAccount);
						that.navigateToDisplayFragment();
					}, 
					function(xhr, textStatus, errorThrown){
					if(typeof xhr.jsonResponse.result!==undefined){
						sap.m.MessageToast.show(xhr.jsonResponse.result);	
					}
					else{
						sap.m.MessageToast.show("Error occured during creation");
					}
				
			}); 
		}else{
			sap.m.MessageToast.show(AppHelper.i18n().getProperty("validation_error_msg"));
		}
		
		
	},
	/** 
	 * @param oEvent
	 */
	getModel:function(sModelName){
		var oModel=this.getView().getModel(sModelName);
		if(!oModel){ 
			oModel=new sap.ui.model.json.JSONModel();
			this.getView().setModel(oModel,sModelName);
		}
		return oModel;
	},
	/**
	 * 
	 * @param oEvent
	 */
	onViewUsersInGroup:function(oEvent){ 
		var sPath=this.byId('vendorGroupList').getSelectedItem().getBindingContext('vu').sPath;
		var oSelectedDocument=this.getModel('vu').getProperty(sPath);
		var oUsersInVendorGroup=Connection.requestData(Configuration.manageVendorAccountServiceUrl.queryVendorUsers+"?vendorGroupID="+oSelectedDocument.vendorGroupID);
		this.getModel("users").setData(oUsersInVendorGroup);
		this._getDialogFragement("UserListDialog").open();
	}, 
	_dialogFragments : {},
	
	/** 
	 * @param sFragmentName
	 * @returns
	 */
	_getDialogFragement:function(sFragmentName){ 
		var oDialogFragment = this._dialogFragments[sFragmentName]; 
		if (oDialogFragment) {
			return oDialogFragment;
		} 
		oDialogFragment = sap.ui.xmlfragment("view.vadmin."+sFragmentName, this);
		this.getView().addDependent(oDialogFragment); 
		return this._dialogFragments[sFragmentName] = oDialogFragment;
	},
	/**
	 * 
	 * @param oEvent
	 */
	onCloseDialog:function(oEvent){
		this._getDialogFragement("UserListDialog").close();
	},
	/**
	 * 
	 */
	
	_formFragments : {},
	/**
	 * 
	 * @param sFragmentName
	 * @returns
	 */
	_getFormFragment : function(sFragmentName) {
		var oFormFragment = this._formFragments[sFragmentName];

		if (oFormFragment) {
			return oFormFragment;
		}
        //Instantiate and implement a callback by adding dependency
		oFormFragment = sap.ui.xmlfragment(this.getView().getId(), 	"view.vadmin." + sFragmentName,this);
		this.getView().addDependent(oFormFragment);
		return this._formFragments[sFragmentName] = oFormFragment;
	},
	/**
	 * 
	 * @param sFragmentName
	 */
	_showFormFragment : function(sFragmentName) {
		var pAccountFragmentPanel = this.getView().byId("accountFragmentTargetPanel"); 
		pAccountFragmentPanel.removeAllContent();
		pAccountFragmentPanel.insertContent(this._getFormFragment(sFragmentName));
	},
	/**
	 * 
	 * @param oEvent
	 */
	onTableSelect:function(oEvent){
		var iSize=oEvent.getSource().getSelectedItems().length; 
		this.toggleAccountButtonsBasedOnSize(iSize);
	},
	/**
	 * 
	 * @param iSize
	 */
	toggleAccountButtonsBasedOnSize:function(iSize){
		if(iSize===1){
			this.toggleButtonVisibility("manageGroups");
		}
		if(iSize===0){
			this.toggleButtonVisibility("hideAll");
		}
		if(iSize>1){
			this.toggleButtonVisibility("hideAll");
		}
	},
	handleEmailValidationSwitch : function(oEvent) {
		var isControlEnabled = oEvent.getSource().getState();
		this.getView().byId("email_domain").setVisible(isControlEnabled); 
	},
	 navigateToDisplayFragment : function() {
		 this.currentAction=this.accountActions.display;
		 this._showFormFragment("DisplayVendorAccount");
		 this.toggleButtonVisibility("viewAccount");
	},
	onExit : function () {
		if (this._oPopover) {
			this._oPopover.destroy();
		}
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
	getUserGroupName :function(iGroupID){
		var result = this.getModel("vu").getData().result;
		for ( var i=0;i<result.length;i++) {
			var oGroup=result[i];
			if(oGroup.vendorGroupID===iGroupID){
				return oGroup.vendorUserGroupDesc;
			}
		} 
	},
	onProfileClosePress:function(oEvent){
		AppHelper.closeProfileDetails(this);
	}
});

