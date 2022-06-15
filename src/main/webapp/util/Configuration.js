/**
 * 
 */
jQuery.sap.declare("Configuration");
Configuration = {};
Configuration.administrator = {
	profileEndPoint : "fpadmin/profile",
};
Configuration.home = "/fp.docs/";
Configuration.manageSetupServiceUrl = {
	queryFairPriceGroups : "fpadmin/settings/fpusergroup/q",
	createFairPriceGroup : "fpadmin/settings/fpusergroup/c",
	vendorCategory : "fpadmin/settings/vc",
	confidentiality : "fpadmin/settings/cl",
	folderStructure : "fpadmin/settings/fs",
	directoryRestrictions : "fpadmin/settings/ds",
	userGroupTypes : "fpadmin/settings/usergroup/t",
	createVendorUserGroup : "fpadmin/settings/vusergroup/c",
	queryVendorUserGroups : "fpadmin/settings/vusergroup/q",
	queryCoorporateDocs : "fpadmin/docs/corporate",
	updateCorporateDoc : "fpadmin/docs/corporate/update",
	createCorporateDoc : "fpadmin/docs/corporate/create",
	documentQuery : "fpadmin/docs/query",
	documentDelete : "fpadmin/docs/delete",
	generalDocumentQuery : "fpadmin/docs/query/general",
	accountStatus:"fpadmin/account/status"
};
Configuration.manageVendorGroupUrl = {
	create : "fpadmin/settings/vusergroup/c",
	query : "fpadmin/settings/vusergroup/q",
	update : "fpadmin/settings/vusergroup/u" 
};
Configuration.manageFairPriceGroupUrl = {
	create : "fpadmin/settings/fpusergroup/c",
	query : "fpadmin/settings/fpusergroup/q",
	update : "fpadmin/settings/fpusergroup/u"
};
Configuration.manageQuotaServiceUrl = {
	queryQuotaAllocations : "fpadmin/quota/q",
	createQuotaAllocation : "fpadmin/quota/c",
	updateQuotaAllocation : "fpadmin/quota/u"
};
Configuration.manageCLevelServiceUrl = {
	query : "fpadmin/clevel/q",
	create : "fpadmin/clevel/c",
	update : "fpadmin/clevel/u"
};

Configuration.manageInternalUsersServiceUrl = {
	queryInitializedUsers : "fpadmin/fuser/q",
	create : "fpadmin/fuser/c",
	update : "fpadmin/fuser/u",
	drop : "fpadmin/fuser/d",
	vendorRel:"fpadmin/fuser/vendor?action="
};
Configuration.manageVendorServiceUrl = {
	query : "fpadmin/fvendors/q",
	create : "fpadmin/fvendors/c",
	update : "fpadmin/fvendors/u",
	drop : "fpadmin/fvendors/d",
	resendEmail : "fpadmin/fvendors/email?vendorID=",
	allowAdminDocumentShare:"fpadmin/fvendors/admin",
	queryAdmin:"fpadmin/fvendors/q/admin?vendorID=",
	queryEmail:"fpadmin/fvendors/q?email=",
	queryVendorCode:"fpadmin/fvendors/q?vendorCode="
};
Configuration.manageVendorAccountServiceUrl = {
	updateVendorAccount : "vadmin/vendorAccount/u",
	addUserToVendorAccount : "vadmin/vendorAccount/add",
	queryVendorUserGroups : "vadmin/vendorAccount/groups",
	queryVendorAccount : "vadmin/vendorAccount/q",
	queryVendorUsers : "vadmin/vendorAccount/vuser/q",
	createVendorUser : "vadmin/vendorAccount/vuser/c",
	updateVendorUser : "vadmin/vendorAccount/vuser/u",
	deleteVendorUser : "vadmin/vendorAccount/vuser/d",
	accountStatus:"vadmin/account/status",
	profileEndPoint : "vadmin/profile",
    requestAssignedGroups:"vadmin/vendorAccount/vuser/q?vendorUserID=",
    checkEmailId:"vadmin/vendorAccount/vuser/q?emailId=",
    deleteGroup:"vadmin/vendorAccount/vuser/q?groupName=",
    requestForAuthorizationGroup:"vadmin/vendorAccount/vuser/role/q",
    assignAuthGroups:"vadmin/vendorAccount/vuser/role/a",
    dropAssignedGroups:"vadmin/vendorAccount/vuser/role/d"
};
Configuration.vendorUserAccountServiceUrl = { 
	queryFunctionalAreas : "vuser/v2FpShare/functionalAreas",
	queryAvailableInternalUsers : "vuser/fpusers/q",
	createServiceRequest : "vuser/v2FpShare/init",
	releaseServiceRequest : "vuser/v2FpShare/release",
	updateServiceRequest : "vuser/v2FpShare/update",
	editServiceRequest : "vuser/v2FpShare/edit?documentID=",
	queryDocumentMembers : "vuser/v2FpShare/q?documentID=",
	keycontactPerson:"vuser/keycontact/available?vendorID=",
	queryCoorporateDocs : "vuser/docs/corporate",
	generalDocumentQuery : "vuser/docs/query/general",
	uploadServiceRequest : "vuser/connect",
	documentQuery : "vuser/docs/query",
	documentDelete : "vuser/docs/delete",
	documentOutBox : "vuser/docs/outbox",
	documentInBox : "vuser/docs/inbox",
	authenticate : "vuser/docs/auth",
	documentDownload : "vuser/docs/download",
	profileEndPoint : "vuser/profile"
};
Configuration.fairPriceUserAccountServiceUrl = {
	queryAvailableVendorGroups : "fpuser/fpusergroup/q",
	queryAvailableVendor : "fpuser/fpvendors/q",
	queryByVendorIDnVendorGroupID : "fpuser/vusers/q",
	createServiceRequest : "fpuser/fp2VShare/init",
	updateServiceRequest : "fpuser/fp2VShare/update",
	editServiceRequest : "fpuser/fp2VShare/edit?documentID=",
	releaseServiceRequest : "fpuser/fp2VShare/release",
	availableParticipant : "fpuser/fp2VShare/participant/available?documentID=",
	availableContributors:"fpuser/fp2VShare/contributor/available?vendorID=",
	addParticipant : "fpuser/fp2VShare/participant/add",
	removeParticipant : "fpuser/fp2VShare/participant/remove",
	queryDocumentMembers : "fpuser/fp2VShare/q?documentID=",
	uploadServiceRequest : "fpuser/connect",
	queryCoorporateDocs : "fpuser/docs/corporate",
	generalDocumentQuery : "fpuser/docs/query/general",
	documentQuery : "fpuser/docs/query",
	documentDelete : "fpuser/docs/delete",
	documentOutBox : "fpuser/docs/outbox",
	documentInBox : "fpuser/docs/inbox",
	authenticate : "fpuser/docs/auth",
	documentDownload : "fpuser/docs/download",
	profileEndPoint : "fpuser/profile"
};
Configuration.general = {
	scimAttributes : "scim/attr?attributeName=" 
};
Configuration.scimAttributes = {
	industry : "industries",
	department : "departments",
	salutation : "salutations"
};