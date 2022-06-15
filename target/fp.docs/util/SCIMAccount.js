jQuery.sap.declare("SCIMAccount");

/**
 * that is SCIMAccount class which is responsible for
 * 
 * @class SCIMAccount
 * 
 */
SCIMAccount = {

	/*
	 * Validate the Account Information before submission
	 */
	validateAccountData : function(data,isVendorUser) {
		var message = "";
		var isValid = true;

		if (!((data.isEdit || data.salutation) && data.firstName
				&& data.lastName && data.displayName
				&& data.email && data.jobTitle
				&& data.work && (isVendorUser || data.vendorCategory))) {
			message = "* Please fill in all required fields" + "\n";
		} else if (!(data.userID && data.vendorCode)) {
			message = "Please add/select an admin" + "\n";
		} else if(!isVendorUser && (!data.selectedGroups || (data.selectedGroups instanceof Array && data.selectedGroups.length === 0))){
			message = "Please assign at least one group to the vendor admin" + "\n";
		}else if(isVendorUser){
			var element;
			for(var i=0; i < data.authorizationList.length && !message;i++){
				element = data.authorizationList[i];
				element.selectedGroups = (element.selectedGroups instanceof Array) ? element.selectedGroups : [];
				if(!(element.vendorCode && element.vendorName) ){
					message = "Please provide Vendor Information for Authorization";
				}else if(element.vendorCode && element.selectedGroups.length === 0){
					message = "Please provide access for Vendor " + element.vendorCode;
				}
			}
		}

		if (message) {
			isValid = false;
			AppHelper.displayAlertMsg("ERROR",message);
		}
		return isValid;
	},
	/***************************************************************************
	 * Get Form Data
	 * 
	 * @method getVendorAccountData
	 * @memberOf util.SCIMAccount
	 */
	getVendorAccountData : function(that) {
		var oFormData = {};
		var isValid = true;

		var salutationInput = that.byId('salutation_input');
		var salutation = salutationInput.mProperties.selectedKey.trim();
		if (salutation.length == 0) {
			salutationInput.setValueState(sap.ui.core.ValueState.Error);
			isValid = false;
		} else {
			salutationInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[salutationInput.getName()] = salutation;
		}

		var firstNameInput = that.byId('firstName_input');
		var firstName = firstNameInput.getValue().trim();
		if (firstName === "") {
			firstNameInput.setValueState(sap.ui.core.ValueState.Error);
			firstNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			firstNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[firstNameInput.getName()] = firstName;
		}

		var lastNameInput = that.byId('lastName_input');
		var lastName = lastNameInput.getValue().trim();
		if (lastName === "") {
			lastNameInput.setValueState(sap.ui.core.ValueState.Error);
			lastNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			lastNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[lastNameInput.getName()] = lastName;
		}

		var displayNameInput = that.byId('displayName_input');
		var displayName = displayNameInput.getValue().trim();
		if (!displayName) {
			displayNameInput.setValueState(sap.ui.core.ValueState.Error);
			displayNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			displayNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[displayNameInput.getName()] = displayName;
		}

		// Work Phone to be made Mandatory
		var noInput = that.byId('workNo_input');
		var tempNo = noInput.getValue().trim();
		if (!tempNo) {
			noInput.setValueState(sap.ui.core.ValueState.Error);
			noInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			noInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[noInput.getName()] = tempNo;
		}

		// Mobile No field is Optional
		noInput = that.byId('mobileNo_input');
		tempNo = noInput.getValue().trim();
		tempNo = (tempNo) ? tempNo : "";
		oFormData[noInput.getName()] = tempNo;

		var jobTitleInput = that.byId('jobTitle_input');
		var jobTitle = jobTitleInput.getValue().trim();
		if (jobTitle === "") {
			jobTitleInput.setValueState(sap.ui.core.ValueState.Error);
			jobTitleInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			jobTitleInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[jobTitleInput.getName()] = jobTitle;
		}

		var emailInput = that.byId('email_input');
		var email = emailInput.getValue().trim();
		if (email === "") {
			emailInput.setValueState(sap.ui.core.ValueState.Error);
			emailInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			if (!AppHelper.isValidEmail(email)) {
				emailInput.setValueState(sap.ui.core.ValueState.Error);
				emailInput.setValueStateText(AppHelper.i18n().getProperty(
						'input_format_msg'));
				isValid = false;
			} else {
				emailInput.setValueState(sap.ui.core.ValueState.None);
				oFormData[emailInput.getName()] = email;
			}
		}

		var userIDInput = that.byId('userID_input');
		var userID = userIDInput.getValue().trim();
		if (userID === "") {
			userIDInput.setValueState(sap.ui.core.ValueState.Error);
			userIDInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			userIDInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[userIDInput.getName()] = userID;
		}

		var vendorCodeInput = that.byId('vendorCode_input');
		var vendorCode = vendorCodeInput.getValue().trim();
		if (vendorCode === "") {
			vendorCodeInput.setValueState(sap.ui.core.ValueState.Error);
			vendorCodeInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			vendorCodeInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[vendorCodeInput.getName()] = vendorCode;
		}

		var companyNameInput = that.byId('companyName_input');
		var companyName = companyNameInput.getValue().trim();
		if (companyName === "") {
			companyNameInput.setValueState(sap.ui.core.ValueState.Error);
			companyNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			companyNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[companyNameInput.getName()] = companyName;
		}

		var vendorCategoryCombo = that.byId('vendorCategory_combo');
		var vendorCategory = vendorCategoryCombo.mProperties.selectedKey.trim();
		if (vendorCategory.length == 0) {
			vendorCategoryCombo.setValueState(sap.ui.core.ValueState.Error);
			isValid = false;
		} else {
			vendorCategoryCombo.setValueState(sap.ui.core.ValueState.None);
			oFormData[vendorCategoryCombo.getName()] = vendorCategory;
		}
		if (!isValid) {
			throw new Error(AppHelper.i18n()
					.getProperty("validation_error_msg"));
		} else {
			return oFormData;
		}
	},
	/**
	 * Get Vendor User details
	 * 
	 * @param that
	 * @returns
	 * @memberOf util.SCIMAccount
	 */
	getVendorUserData : function(that) {
		var oFormData = {};
		var isValid = true;

		var salutationInput = that.byId('salutation_input');
		var salutation = salutationInput.mProperties.selectedKey.trim();
		if (salutation.length == 0) {
			salutationInput.setValueState(sap.ui.core.ValueState.Error);
			isValid = false;
		} else {
			salutationInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[salutationInput.getName()] = salutation;
		}

		var vendorCodeCombo = that.byId('vendorCode_combo');
		var vendorCode = vendorCodeCombo.mProperties.selectedKey.trim();
		if (vendorCode.length == 0) {
			vendorCodeCombo.setValueState(sap.ui.core.ValueState.Error);
			isValid = false;
		} else {
			vendorCodeCombo.setValueState(sap.ui.core.ValueState.None);
			oFormData[vendorCodeCombo.getName()] = vendorCode;
		}

		var firstNameInput = that.byId('firstName_input');
		var firstName = firstNameInput.getValue().trim();
		if (firstName === "") {
			firstNameInput.setValueState(sap.ui.core.ValueState.Error);
			firstNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			firstNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[firstNameInput.getName()] = firstName;
		}

		var lastNameInput = that.byId('lastName_input');
		var lastName = lastNameInput.getValue().trim();
		if (lastName === "") {
			lastNameInput.setValueState(sap.ui.core.ValueState.Error);
			lastNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			lastNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[lastNameInput.getName()] = lastName;
		}

		var displayNameInput = that.byId('displayName_input');
		var displayName = displayNameInput.getValue().trim();
		if (displayName === "") {
			displayNameInput.setValueState(sap.ui.core.ValueState.Error);
			displayNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			displayNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[displayNameInput.getName()] = displayName;
		}

		// Work Phone to be made Mandatory
		var noInput = that.byId('workNo_input');
		var tempNo = noInput.getValue().trim();
		oFormData.addedProperty = {};
		if (!tempNo) {
			noInput.setValueState(sap.ui.core.ValueState.Error);
			noInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			noInput.setValueState(sap.ui.core.ValueState.None);
			oFormData.addedProperty[noInput.getName()] = tempNo;
		}

		// Mobile No field is Optional
		noInput = that.byId('mobileNo_input');
		tempNo = noInput.getValue().trim();
		tempNo = (tempNo) ? tempNo : "";
		oFormData.addedProperty[noInput.getName()] = tempNo;

		var jobTitleInput = that.byId('jobTitle_input');
		var jobTitle = jobTitleInput.getValue().trim();
		if (jobTitle === "") {
			jobTitleInput.setValueState(sap.ui.core.ValueState.Error);
			jobTitleInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			jobTitleInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[jobTitleInput.getName()] = jobTitle;
		}
		var emailInput = that.byId('email_input');
		var email = emailInput.getValue().trim();
		if (email === "") {
			emailInput.setValueState(sap.ui.core.ValueState.Error);
			emailInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			if (!AppHelper.isValidEmail(email)) {
				emailInput.setValueState(sap.ui.core.ValueState.Error);
				emailInput.setValueStateText(AppHelper.i18n().getProperty(
						'input_format_msg'));
				isValid = false;
			} else {
				emailInput.setValueState(sap.ui.core.ValueState.None);
				oFormData[emailInput.getName()] = email;
			}
		}

		var userIDInput = that.byId('userID_input');
		var userID = userIDInput.getValue().trim();
		if (userID === "") {
			userIDInput.setValueState(sap.ui.core.ValueState.Error);
			userIDInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			userIDInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[userIDInput.getName()] = userID;
		}

		var vendorUserGroupIDCombo = that.byId('userGroup_combo');
		var vendorUserGroupID = vendorUserGroupIDCombo.getSelectedKeys();
		if (vendorUserGroupID.length == 0) {
			vendorUserGroupIDCombo.setValueState(sap.ui.core.ValueState.Error);
			isValid = false;
		} else {
			vendorUserGroupIDCombo.setValueState(sap.ui.core.ValueState.None);
			oFormData[vendorUserGroupIDCombo.getName()] = vendorUserGroupID;
		}

		if (!isValid) {
			throw new Error(AppHelper.i18n()
					.getProperty("validation_error_msg"));
		} else {
			return oFormData;
		}
	},
	/**
	 * Get the internal user data for SCI account creation
	 * 
	 * @param that
	 */
	getInternalUserData : function(that) {
		var oFormData = {};
		var isValid = true;

		var salutationInput = that.byId('salutation_input');
		var salutation = salutationInput.mProperties.selectedKey.trim();
		if (salutation.length == 0) {
			salutationInput.setValueState(sap.ui.core.ValueState.Error);
			isValid = false;
		} else {
			salutationInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[salutationInput.getName()] = salutation;
		}

		var firstNameInput = that.byId('firstName_input');
		var firstName = firstNameInput.getValue().trim();
		if (firstName === "") {
			firstNameInput.setValueState(sap.ui.core.ValueState.Error);
			firstNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			firstNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[firstNameInput.getName()] = firstName;
		}

		var lastNameInput = that.byId('lastName_input');
		var lastName = lastNameInput.getValue().trim();
		if (lastName === "") {
			lastNameInput.setValueState(sap.ui.core.ValueState.Error);
			lastNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			lastNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[lastNameInput.getName()] = lastName;
		}

		var displayNameInput = that.byId('displayName_input');
		var displayName = displayNameInput.getValue().trim();
		if (displayName === "") {
			displayNameInput.setValueState(sap.ui.core.ValueState.Error);
			displayNameInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			displayNameInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[displayNameInput.getName()] = displayName;
		}
		var emailInput = that.byId('email_input');
		var email = emailInput.getValue().trim();
		if (email === "") {
			emailInput.setValueState(sap.ui.core.ValueState.Error);
			emailInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			if (!AppHelper.isValidEmail(email)) {
				emailInput.setValueState(sap.ui.core.ValueState.Error);
				emailInput.setValueStateText(AppHelper.i18n().getProperty(
						'input_format_msg'));
				isValid = false;
			} else {
				emailInput.setValueState(sap.ui.core.ValueState.None);
				oFormData[emailInput.getName()] = email;
			}
		}

		var userIDInput = that.byId('userID_input');
		var userID = userIDInput.getValue().trim();
		if (userID === "") {
			userIDInput.setValueState(sap.ui.core.ValueState.Error);
			userIDInput.setValueStateText(AppHelper.i18n().getProperty(
					'input_empty_msg'));
			isValid = false;
		} else {
			userIDInput.setValueState(sap.ui.core.ValueState.None);
			oFormData[userIDInput.getName()] = userID;
		}

		var userGroupIDCombo = that.byId('userGroup_combo');
		var userGroupID = userGroupIDCombo.mProperties.selectedKey.trim();
		if (userGroupID.length == 0) {
			userGroupIDCombo.setValueState(sap.ui.core.ValueState.Error);
			isValid = false;
		} else {
			userGroupIDCombo.setValueState(sap.ui.core.ValueState.None);
			oFormData[userGroupIDCombo.getName()] = userGroupID;
		}

		if (!isValid) {
			throw new Error(AppHelper.i18n()
					.getProperty("validation_error_msg"));
		} else {
			return oFormData;
		}
	}

}