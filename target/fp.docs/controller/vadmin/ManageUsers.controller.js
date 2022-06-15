sap.ui.controller("controller.vadmin.ManageUsers", {

	/**
	 * Called when a controller is instantiated and its View controls (if available) are already created.
	 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
	 * @memberOf controller.vadmin.ManageUsers
	 */
	onInit: function() {
		this.oInitialLoadFinishedDeferred = jQuery.Deferred();
		this.router = sap.ui.core.UIComponent.getRouterFor(this);
		this.router.attachRoutePatternMatched(this.onRouteMatched, this);
		AppHelper.initProfileDetails(this);

	},
	/**
	 * User actions
	 */
	actions: {
		edit: "edit",
		create: "create",
		none: "none"
	},
	
	onRouteMatched: function(oEvent) {
		var sName = oEvent.getParameter("name");
		if (sName !== 'admin_users') {
			return;
		} else {
			this._showFragment("UserList");
			this.loadRequiredData();
			this.toggleToListViewButton();
			var oUserData={action:this.actions.none};
			this.getModel("user").setData(oUserData);
		}
	},
	/**
	 * Initialize User List model
	 * 
	 * @method initializeUsersModel
	 */
	initializeUsersModel: function() {
		var usersListModel = this.getModel("users");
		usersListModel.setDefaultBindingMode("OneWay");
		usersListModel.setData(Connection.requestData(Configuration.manageVendorAccountServiceUrl.queryVendorUsers));
		
		//Maintain the Count of Vendor Account against each vendor code
		var userData = usersListModel.getData();
		var accountProperty = {};
		var userElement;
		for (var u = 0; userData.result instanceof Array && u < userData.result.length; u++) {
			userElement = userData.result[u];
			accountProperty[userElement.vendorCode] = (!accountProperty[userElement.vendorCode])? 1 : (accountProperty[userElement.vendorCode] + 1);
		}
		userData.existingAccounts = accountProperty;
	},

	loadRequiredData: function() {
		AppHelper.showLoader();
		this.initializeUsersModel();
//		this.getModel('users').setData(Connection.requestData(Configuration.manageVendorAccountServiceUrl.queryVendorUsers));
		this.getModel('ug').setData(Connection.requestData(Configuration.manageVendorAccountServiceUrl.queryVendorUserGroups));
		this.getModel("status").setData(sap.ui.getCore().getModel('status').getData());
		this.getModel("salutation").setData(sap.ui.getCore().getModel('salutation').getData());
		this.loadUnassignedAuthGroups();
		AppHelper.hideLoader();
	},
	
	/**
	 * Load Vendor User Lookups - Application Groups and Vendor List 
	 */
	loadUnassignedAuthGroups: function() {
		AppHelper.showLoader();
		var that = this;
		var url=Configuration.manageVendorAccountServiceUrl.requestForAuthorizationGroup;
		Connection.requestWithCallbackData(url, function(sMessage) {
			AppHelper.displayMsgAlert(AppHelper.messageType.error, sMessage);
			AppHelper.hideLoader();
		}, function(data) {
			AppHelper.hideLoader();
			that.getModel(that.models.appAuthGroups).setData(data);
			//Groups and Vendor List Assignments
			var groupData = (data && data.resultType === "SUCCESS" && data.relMap) ? that.parseJsonData(data.relMap) : null;
			if(groupData.vendorList instanceof Array){
				groupData.vendorList.push(["",""]);
			}
			that.getModel("accountInfo").setData(groupData);
		});
	},
	
	/**
	 * Called when the new vendor button is clicked.
	 * 
	 * @memberOf controller.vadmin.ManageUsers
	 */
	onCreateNewUserPress: function(oEvent) {
//		var users = this.getModel("users").getData();
//		if(users.result instanceof Array && users.result.length <= 20){
			this.toggleToSaveButton();
			this.oUserData = {};
			this.oUserData.action = this.actions.create;
			this.oUserData.isEdit = false;
			this.getModel("user").setData(this.oUserData);
			this._showFragment("ManageVendorUser");
//		}else{
//			AppHelper.displayMsgAlert(AppHelper.i18n().getProperty("validation_acccreation_msg") );
//		}
	},
	/**
	 * Add Vendor User Entries on the screen
	 */
	onPressAddVendorUsers: function() {
		var element = {};
		element.vendorCode = "";
		element.vendorName = "";
		element.isVendorUser = false;
		element.isDelete = true;
		element.isVendorCode = true;
		var vendorUserModel = this.getModel("user");
		var userData = vendorUserModel.getData();
//		element.isCreate = (userData.action === this.actions.create);
		element.isCreate = false;
		userData.authorizationList = (userData.authorizationList instanceof Array) ? userData.authorizationList : [];
		userData.authorizationList.push(element);
		vendorUserModel.setData(userData);
	},
	/**
	 * On Select Vendor Code 
	 */
	selectVendorCode : function(oEvent){
		var selectedProp = oEvent.getParameter('selectedItem').getProperty("key");
		var vendorName = this.getVendorDetails(selectedProp);
		var sPath = oEvent.oSource.oParent.oBindingContexts.user.getPath();
		var userModel = this.getModel("user");
		var userData = userModel.getData();
		var userList = userModel.getProperty("/authorizationList");
		userModel.setProperty(sPath + "/vendorName",vendorName);
		
		userModel.setProperty(sPath + "/isCreate",(selectedProp)? (userData.action === this.actions.create) : false);
		var element,codeProp = {};
		var isDuplicate = false;
		for (var s = 0; s < userList.length; s++) {
			element = userList[s];
			element.vState = sap.ui.core.ValueState.None;
			if (element.vendorCode) {
				if (!codeProp[element.vendorCode]) {
					codeProp[element.vendorCode] = element.vendorCode;
				} else {
					isDuplicate = true;
					element.vState = sap.ui.core.ValueState.Error;
					selectedText = "";
					break;
				}
			}
		}
		
		var isPrimary = userModel.getProperty(sPath + "/isVendorUser");
		isPrimary = (selectedProp && !isDuplicate)? isPrimary :  false; 
		userModel.setProperty(sPath + "/isVendorUser",isPrimary);
		
		if(isPrimary && selectedProp){
			userModel.setProperty("/vendorCode", selectedProp);
			userModel.setProperty("/vendorName", (vendorName)? vendorName : "");
		}
		
		if(isDuplicate){
			AppHelper.displayToast("Access already provided for  "+selectedProp +" , please select another Vendor");
			userModel.setProperty(sPath + "/vendorCode","");
			userModel.setProperty(sPath + "/vendorName","");
		}
	},
	/**
	 * Get Vendor Name
	 */
	getVendorDetails : function(vendorCode){
		var vendorName = "";
		if(vendorCode){
			var vendorList = this.getModel("accountInfo").getProperty("/vendorList");
			if(vendorList instanceof Array && vendorList.length > 1){
				var element;
				for (var i = 0; i < vendorList.length; i++) {
					element = vendorList[i];
					if(element instanceof Array && vendorCode === element[0]){
						vendorName = element[1];
						break;
					}
				}
			}
		}
		return vendorName;
	},
	/**
	 * On Press Delete Access Entries from Table 
	 */
	onPressDelete : function(oEvent){
		var selectedModel = oEvent.oSource.oParent.oBindingContexts.user;
		var sPath = selectedModel.sPath;
		selectedModel = selectedModel.oModel;
		var userDetails = selectedModel.getProperty(sPath);
		var idx = sPath.charAt(sPath.lastIndexOf('/') + 1);
		idx = Number(idx);
		if (idx !== -1) {
			var tmpData = selectedModel.getData();
			tmpData.authorizationList.splice(idx, 1);
			if(userDetails && userDetails.isVendorUser){
				tmpData.vendorCode = "";
			}
			selectedModel.setData(tmpData);
		}
	},
	/*
	 * Select any one to associate the Primary account
	 */
	selectUser: function(oEvent) {
		var selectedModel = oEvent.oSource.oParent.oBindingContexts.user;
		var sPath = selectedModel.sPath;
		selectedModel = selectedModel.oModel;
		var userDetails = selectedModel.getProperty(sPath);
		var isValid = true;
		selectedModel.setProperty("/vendorName");
		selectedModel.setProperty("/vendorCode");
		
		if (userDetails.vendorCode) {
			this.validateUserAccount(userDetails);
			selectedModel.setProperty("/vendorName",(userDetails.isVendorUser) ? userDetails.vendorName : "");
			selectedModel.setProperty("/vendorCode",(userDetails.isVendorUser)? userDetails.vendorCode : "");
		} else {
			isValid = false;
			var oTarget = this.getView().byId('vendorListPage');
			AppHelper.showMsgStrip(oTarget, "Error", "Please provide vendor code for the admin");
		}
		
		if(userDetails.isVendorUser){
			//Selection of any Vendor Admin Code
			var authorizationList = selectedModel.getProperty("/authorizationList");
			Formatter.selectOneItem(sPath, "isVendorUser", selectedModel, authorizationList, isValid);
		}
		
		//Account Validation
		
	},
	/**
	 * Validate User Account Creation against Vendor Code 
	 */
	validateUserAccount : function(selectedData){
		var userListData = this.getModel("users").getData();
		if(Number(userListData.existingAccounts[selectedData.vendorCode]) >= 20){
			selectedData.isVendorUser = false;
			var message = AppHelper.i18n().getProperty("validation_acccreation_msg1")  + " " +
			selectedData.vendorCode + " " + AppHelper.i18n().getProperty("validation_acccreation_msg2"); 
			AppHelper.displayMsgAlert(message);
		}
	},
	/**
	 * Create Corporate Document Event
	 * @param oEvent
	 */
	getModel: function(oModelName) {
		var oModel = this.getView().getModel(oModelName);
		if (!oModel) {
			oModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(oModel, oModelName);
		}
		return oModel;
	},

	onItemSelectEvent: function(oEvent) {
		this.toggleToListViewButton();
	},
	onCloseDialog: function() { 
		if (this.oAvailableGroupsDialog) {
			this._getAvailableGroupDialog().close();
		}
	},

	handleUploadComplete: function(oEvent) {
		var sResponse = oEvent.getParameter("response");
		if (sResponse) {
			var sMsg = "";
			var m = /^\[(\d\d\d)\]:(.*)$/.exec(sResponse);
			if (m[1] == "200") {
				sMsg = "Return Code: " + m[1] + "\n" + m[2], "SUCCESS", "Upload Success";
				oEvent.getSource().setValue("");
			} else {
				sMsg = "Return Code: " + m[1] + "\n" + m[2], "ERROR", "Upload Error";
			}
			jQuery.sap.require("sap.m.MessageToast");
			sap.m.MessageToast.show(sMsg);
		}
	},

	onExit: function() {
		if (this._oPopover) {
			this._oPopover.destroy();
		}
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
	
	/*
	 * On Press Save Vendor User Details
	 */
	onPressSaveVendorUserDetails : function(){
		var userData = this.getModel("user").getData();
		//Validate the Account Details
		if(SCIMAccount.validateAccountData(userData,true)){
			var that = this;
			
			var url = (userData.action === this.actions.create)?  Configuration.manageVendorAccountServiceUrl.createVendorUser : Configuration.manageVendorAccountServiceUrl.updateVendorUser;
//			var url = Configuration.manageVendorAccountServiceUrl.createVendorUser;updateVendorUser
				try {
					var result="Error occured during creation of Vendor";
					var data = this.frameAccountData(userData);
					Connection.postData(url, data,
							function(response) {
						if(response.resultType === "SUCCESS"){
							var message = (userData.action === that.actions.create)? AppHelper.i18n().getProperty("success_user_msg") : 
							AppHelper.i18n().getProperty("success_userupdate_msg");
							AppHelper.displayMsgAlert(message + " "+ userData.vendorName,"Success");
							that.initializeUsersModel();
							that.onCancelUserModifyEvent();
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
	 * Frame Vendor User Data
	 */
	frameAccountData: function(userData){
		var accountData = this.parseJsonData(userData);
//		accountData.userID = accountData.userName;
		accountData.userID = accountData.userID + "@" + accountData.vendorCode;
		accountData.loginName = accountData.userID + "@" + accountData.vendorCode;
		accountData.sciUserId = (accountData.sciAccountID)? accountData.sciAccountID : "";
		accountData.systemAccountStatus = accountData.systemAccountStatusUpdated;
//		if(accountData.action !== this.actions.edit){
//			delete(accountData.action);
//		}
		var properties = [/*"systemAccountStatusUpdated",*/"systemUserRole","quotaUtilization","isEdit","vendorUserEmail",
			"validateEmailDomain","sciAccountID","activatedBy","createDateTime","accountValidated","createdBy","userName"];
		for(var i=0;i<=properties.length;i++){
			delete(accountData[properties[i]]);
		}
		
		var tempAuthorizations = accountData.authorizationList;
		accountData.vendorAccounts = []; //This representation is same as used in Vendor Administrator service
		
		if(tempAuthorizations instanceof Array){
			var element;
			for (var j = 0; j < tempAuthorizations.length; j++) {
				element = {};
				element.vendorID = (tempAuthorizations[j].vendorID)? tempAuthorizations[j].vendorID : "";
				element.vendorCode = tempAuthorizations[j].vendorCode;
				element.vendorName = tempAuthorizations[j].vendorName;
				element.groupsAssigned = (tempAuthorizations[j].selectedGroups instanceof Array)? tempAuthorizations[j].selectedGroups.join(",") : "";
				element.isVendorUser = (tempAuthorizations[j].isVendorUser)? true : false;
				accountData.vendorAccounts.push(element);
			}
		}
		return accountData;
	},

	onSaveUserEvent: function(oEvent) {
		var selectedAction = oEvent.getSource().getAggregation("customData")[0].getProperty("value");
		var that = this;
		if (selectedAction == this.actions.create) {
			try {
				var url = Configuration.manageVendorAccountServiceUrl.createVendorUser;
				SCIMAccount.getVendorUserData(this);
				var oVendorUser = this.getModel("user").getData();
				Connection.postData(url, oVendorUser,
					function(response) {
						that.getModel('users').setData(response);
						that.onCancelUserModifyEvent();
					},
					function(xhr, textStatus, errorThrown) {
						var result = xhr.responseJSON.result;
						if (typeof result !== "undefined") {
							AppHelper.displayMsgAlert(result);
						} else {
							AppHelper.displayMsgAlert("Error occured");
						}

					});
			} catch (e) {
				AppHelper.displayMsgAlert(e.message);
			}
		} else {
			var isValid = true;
			var firstNameInput = this.byId('first_name_input');
			var firstName = firstNameInput.getValue().trim();
			if (firstName === "") {
				firstNameInput.setValueState(sap.ui.core.ValueState.Error);
				isValid = false;
			} else {
				firstNameInput.setValueState(sap.ui.core.ValueState.None);
			}

			var lastNameInput = this.byId('last_name_input');
			var lastName = lastNameInput.getValue().trim();
			if (lastName === "") {
				lastNameInput.setValueState(sap.ui.core.ValueState.Error);
				isValid = false;
			} else {
				lastNameInput.setValueState(sap.ui.core.ValueState.None);
			}

			var userIDPUIDInput = this.byId('user_sap_serviceid_input');
			var userIDPUID = userIDPUIDInput.getValue().trim();
			if (userIDPUID === "") {
				userIDPUIDInput.setValueState(sap.ui.core.ValueState.Error);
				isValid = false;
			} else {
				userIDPUIDInput.setValueState(sap.ui.core.ValueState.None);
			}

			var vendorUserEmailInput = this.byId('vendorUserEmail_input');
			var vendorUserEmail = vendorUserEmailInput.getValue().trim();
			if (vendorUserEmail === "") {
				vendorUserEmailInput.setValueState(sap.ui.core.ValueState.Error);
				isValid = false;
			} else {
				if (!AppHelper.isValidEmail(vendorUserEmail)) {
					vendorUserEmailInput.setValueState(sap.ui.core.ValueState.Error);
					isValid = false;
				} else {
					vendorUserEmailInput.setValueState(sap.ui.core.ValueState.None);
				}
			}

			var vendorGroupIDCombo = this.byId('user_group_type_combo');
			var vendorGroupID = vendorGroupIDCombo.getSelectedKeys();
			if (vendorGroupID.length == 0) {
				vendorGroupIDCombo.setValueState(sap.ui.core.ValueState.Error);
				isValid = false;
			} else {
				vendorGroupIDCombo.setValueState(sap.ui.core.ValueState.None);
			}

			if (isValid) {
				var url = Configuration.manageVendorAccountServiceUrl.updateVendorUser;
				var oVendorUser = this.getModel("user").getData();
				Connection.postData(url, oVendorUser,
					function(response) {
						that.getModel('users').setData(response);
						that.onCancelUserModifyEvent();
					},
					function(xhr, textStatus, errorThrown) {
						var result = xhr.responseJSON.result;
						if (typeof result !== "undefined") {
							AppHelper.displayMsgAlert(result);
						} else {
							AppHelper.displayMsgAlert("Error occured");
						}
					});

			} else {
				sap.m.MessageToast.show(AppHelper.i18n().getProperty("validation_error_msg"));
			}
		}

	},
	_resetUserInitializationForm: function() {
		this.byId('salutation_input').setValue("");
		this.byId('firstName_input').setValue("");
		this.byId('lastName_input').setValue("");
		this.byId('displayName_input').setValue("");
		this.byId('jobTitle_input').setValue("");
		this.byId('userID_input').setValue("");
		this.byId("userGroup_combo").setSelectedKeys(null);
		this.byId("userGroup_combo").setSelectedItems(null);
	},

	_fragments: {},

	_getFragment: function(sFragmentName) {
		var oFormFragment = this._fragments[sFragmentName];

		if (oFormFragment) {
			return oFormFragment;
		}
		//Instantiate and implement a callback by adding dependency
		oFormFragment = sap.ui.xmlfragment(this.getView().getId(), "view.vadmin." + sFragmentName, this);
		this.getView().addDependent(oFormFragment);
		return this._fragments[sFragmentName] = oFormFragment;
	},

	_showFragment: function(sFragmentName) {
		var oPage = this.getView().byId("userDetailPage");
		oPage.removeAllContent();
		oPage.insertContent(this._getFragment(sFragmentName));
	},
	onCancelUserModifyEvent: function(oEvent) {
		this.toggleToListViewButton();
		var model=this.getModel("user");
		var data=model.getData();
		data.action=this.actions.none;
		model.setData(data);
		this._showFragment("UserList");
	}, 
	
	selectedVendorGroupKeys: function(oSelectedVendorGroups) {
		var selectedKeys = [];
		for (var i in oSelectedVendorGroups) {
			selectedKeys[i] = oSelectedVendorGroups[i].vendorGroupID;
		}
		return selectedKeys;
	},
	
	onEditUserEvent: function(oEvent) { 
		this.oUserData = this.getModel('users').getProperty(this.byId('userList').getSelectedItem().getBindingContext('users').sPath);
		this.oUserData.action = this.actions.edit;
		var userData = this.parseJsonData(this.oUserData);
		this.getModel("user").setData(userData);
		this.toggleToSaveButton();
		this.retrieveAssignedGroups(this.oUserData);
	},
	/**
	 * Retrieve Assignments and SCI Details pertaining to Vendor User 
	 */
	retrieveAssignedGroups:function(oUser){
	    AppHelper.showLoader();
		var that = this;
		var url=Configuration.manageVendorAccountServiceUrl.requestAssignedGroups+oUser.vendorUserID;
		Connection.requestWithCallbackData(url, function(sMessage) {
			AppHelper.displayMsgAlert(AppHelper.messageType.error, sMessage);
			AppHelper.hideLoader();
		}, function(data) {
			AppHelper.hideLoader();
			data = (data.result && data.result.relMap)? that.parseJsonData(data.result.relMap) : null;
			
			if(data){
				Formatter.formatVendorUserData(that.getModel("user"), data);
				that._showFragment("ManageVendorUser");
			}
			
//			that.getModel(that.models.assignedAuthGroups).setData(data);
//			that.deductAssignableGroup();
		});
	},
	/**
	 * On Press Delete Vendor User Account
	 */
	onDeleteUserEvent: function(oEvent) {
		var that = this;
		sap.m.MessageBox.show(AppHelper.i18n().getProperty("msg_delete_user"), {
			icon: sap.m.MessageBox.Icon.ERROR,
			title: AppHelper.i18n().getProperty("title_delete_user"),
			actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
			onClose: function(oAction) {
				if (oAction === sap.m.MessageBox.Action.YES) {
					var oUserList = that.byId('userList');
					var sPath = oUserList.getSelectedItem().getBindingContext('users').sPath;
					var oVendorUser = that.getModel('users').getProperty(sPath);
					var url = Configuration.manageVendorAccountServiceUrl.deleteVendorUser;
					Connection.postData(url, oVendorUser,
						function(response) {
//							that.getView().getModel('users').setData(response);
							that.initializeUsersModel();
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
				}
			}
		});

	},
	toggleToSaveButton: function() {
		this.byId("btn_edit_user").setVisible(false);
		this.byId("btn_new_user").setVisible(false);
		this.toggleDeleteButton(false);
		this.byId("btn_cancel_user").setVisible(true);
		this.byId("btn_save_user").setVisible(true);
	},
	toggleDeleteButton: function(isVisible) {
		this.byId("btn_delete_user").setVisible(isVisible);
	},
	toggleToListViewButton: function() {
		var showContextActions = this.byId('userList').getSelectedItems().length > 0;
		this.byId("btn_edit_user").setVisible(showContextActions);
		this.toggleDeleteButton(showContextActions);
		this.byId("btn_new_user").setVisible(true);
		this.byId("btn_cancel_user").setVisible(false);
		this.byId("btn_save_user").setVisible(false);
	},
	getFragmentTitle: function(sAction) {
		var sDailogTitle = AppHelper.i18n().getProperty("title_create_user");
		if (sAction === this.actions.edit) {
			sDailogTitle = AppHelper.i18n().getProperty("title_update_user");
		}
		return sDailogTitle;
	},
	
	/**
	 * Show Group Section
	 * @param sAction
	 * @returns {Boolean}
	 */
	showGroupSection: function(sAction) {
		if(sAction){
			return (sAction === this.actions.edit);
		}
		return false;
	},
	/**
	 * Provision User to Composite Group
	 * 
	 */
	provisionUserToCompositeAuthGroup: function() {
		this._getAvailableGroupDialog().open();
	},
	/**
	 * 
	 */
	_getAvailableGroupDialog: function() {
		if (!this.oAvailableGroupsDialog) {
			// create dialog via fragment factory
			this.oAvailableGroupsDialog = sap.ui.xmlfragment("view.vadmin.AvailableGroupListDialog", this);
			// connect dialog to view (models, lifecycle)
			this.getView().addDependent(this.oAvailableGroupsDialog);
		}
		return this.oAvailableGroupsDialog;
	},
	/**
	 * Deduct Assignable Groups
	 */
	deductAssignableGroup: function() {
		var oAssignedGroupsData = this.getModel(this.models.assignedAuthGroups).getData();
		var sciGroupData = this.getModel(this.models.appAuthGroups).getData();
		var deductedGroupsModel = this.getModel(this.models.deductedGroups);
		var oDedicatedGroupData = {
			result: []
		};
		for (var itemIndex in sciGroupData.result) {
			var oCurrentGroup = sciGroupData.result[itemIndex];
			if (!this.isGroupAlreadyAllocated(oAssignedGroupsData, oCurrentGroup)) {
				oDedicatedGroupData.result.push(oCurrentGroup);
			}
		}
		deductedGroupsModel.setData(oDedicatedGroupData);
	},
	/**
	 * Add Group from collection
	 */
	isGroupAlreadyAllocated: function(oAllocatedGroupsData, oCurrentGroup) {
		for (var itemIndex in oAllocatedGroupsData.result) {
			var itemInIndex=oAllocatedGroupsData.result[itemIndex];
			if (itemInIndex.id === oCurrentGroup.id) {
				return true;
			}
		}
		return false;
	},
	/**
	 * Models
	 */
	models: {
		appAuthGroups: "appAuthGroups",
		assignedAuthGroups: "assignedAuthGroups",
		deductedGroups: "deductedGroups"
	},
	/**
	 * On add Group Composite
	 * 
	 */
	onAddGroupToUser: function() {
		var that = this;
		var oCompositeVendorData = {
			collection: [],
			vendorUser:this.oUserData
		};
		
		var sciAuthGroupList = sap.ui.getCore().byId("sciGroupsID");
		var selectedItems = sciAuthGroupList.getSelectedItems();
		for (var itemIndex in selectedItems) {
			var sBindingPath = selectedItems[itemIndex].getBindingContext(this.models.deductedGroups).sPath;
			oCompositeVendorData.collection.push(that.getView().getModel(this.models.deductedGroups).getProperty(sBindingPath));
		}
		////////////////////////////// 
		AppHelper.showLoader();
		setTimeout(function() {
			var url = Configuration.manageVendorAccountServiceUrl.assignAuthGroups;
			Connection.postData(url, oCompositeVendorData, function(data) {
				that.getModel(that.models.assignedAuthGroups).setData(data);
				sciAuthGroupList.removeSelections(true);
				that.deductAssignableGroup();
				that.onCloseDialog();
				AppHelper.hideLoader();
			}, function(xhr, sMessage) {
				AppHelper.displayMsgAlert(AppHelper.messageType.error, sMessage);
				AppHelper.hideLoader();
			});
		}, 3000);

	},
	/**
	 * Remove authorization group from Composite
	 */
	onRemoveAuthGroupFromUser: function(oEvent) {
		var that = this;
		var oCompositeVendorData = {
			collection: [],
			vendorUser:this.oUserData
		};
		var sPath = oEvent.getParameter("listItem").getBindingContext(this.models.assignedAuthGroups).sPath;
		var droppedGroupData = this.getModel(this.models.assignedAuthGroups).getProperty(sPath);
		oCompositeVendorData.collection.push(droppedGroupData);
		AppHelper.showLoader(); 
		setTimeout(function() {
			var url = Configuration.manageVendorAccountServiceUrl.dropAssignedGroups;
			Connection.postData(url, oCompositeVendorData, function(data) {
				that.getView().getModel(that.models.assignedAuthGroups).setData(data);
				that.deductAssignableGroup();
				AppHelper.hideLoader();
			}, function(xhr, sMessage) {
				AppHelper.displayMsgAlert(AppHelper.messageType.error, sMessage);
				AppHelper.hideLoader();
			});
		}, 3000);
	},
	onFilterRequest: function(oEvent) {
		var aFilters = [];
		var sQuery = oEvent.getSource().getValue();
		if (sQuery && sQuery.length > 0) {
			var filter = new sap.ui.model.Filter("vendorUserFullname", sap.ui.model.FilterOperator.Contains, sQuery);
			aFilters.push(filter);
		}
		var list = this.getView().byId("userList");
		var binding = list.getBinding("items");
		binding.filter(aFilters, "Application");
	},
	getUserGroupName: function(iGroupID) {
		var result = this.getView().getModel("ug").getData().result;
		for (var i = 0; i < result.length; i++) {
			var oGroup = result[i];
			if (oGroup.vendorGroupID === iGroupID) {
				return oGroup.vendorUserGroupDesc;
			}
		}
	},
	onProfileClosePress: function(oEvent) {
		AppHelper.closeProfileDetails(this);
	},
	/**
	 * Check for Special Characters
	 */ 
	isSpecialCharacter: function(oEvent) {
		var objId = oEvent.getSource().getId();
		var userName = sap.ui.getCore().getControl(objId).getValue().trim();
		var mailregex = /^[a-zA-Z0-9]*$/;
		if (mailregex.test(userName)) {
			sap.ui.getCore().getControl(objId).setValueState(sap.ui.core.ValueState.None);
		} else {
			sap.ui.getCore().getControl(objId).setValueState(sap.ui.core.ValueState.Error);
			sap.ui.getCore().getControl(objId).setValue("");
			AppHelper.displayAlertMsg(AppHelper.messageType.error, "Please avoid special characters for User Id");
		}
	},
	
	/**
	 * On Change Email Address
	 */
	onChangeEmailAddress: function(oEvent) {
		var emailCtrl = this.getUIControl(oEvent.getSource().getId());
		if (emailCtrl.getValue()) {
			//Check for Existing Email ID
			var url=Configuration.manageVendorAccountServiceUrl.checkEmailId+emailCtrl.getValue();
			this.checkForExisting(url, emailCtrl);
		}
	},
	/**
	 * Validate against Email and Vendor Code
	 */
	checkForExisting: function(url, control) {
		var that = this;
		var response = Connection.requestData(url);
		if(response.resultType === "EXISTS"){
			message = "Account with Email ID : "+control.getValue()+ " already exists in the system";
			control.setValue("");
			AppHelper.displayAlertMsg("ERROR",message);
		}
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
	 * Parse Object
	 */
	parseJsonData: function(data) {
		if (data) {
			data = JSON.parse(JSON.stringify(data));
		}
		return data;
	}
	 
});