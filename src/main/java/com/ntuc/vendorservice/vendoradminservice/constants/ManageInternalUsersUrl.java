package com.ntuc.vendorservice.vendoradminservice.constants;

public enum ManageInternalUsersUrl {
	 INTERNAL_USERS_QUERY("/fp.docs/fpadmin/fuser/q"),
	 INTERNAL_USER_CREATE("/fp.docs/fpadmin/fuser/c"),
	 INTERNAL_USER_VENDOR_REL("/fp.docs/fpadmin/fuser/vendor"),
	 INTERNAL_USER_UPDATE("/fp.docs/fpadmin/fuser/u"),
	 INTERNAL_USER_DELETE("/fp.docs/fpadmin/fuser/d"),
	 UNKNOWN("UNKNOWN");
	
	protected String value;

	ManageInternalUsersUrl(String value) {
		this.value = value;
	}

	public static ManageInternalUsersUrl fromValue(String value) {
		for (ManageInternalUsersUrl type : ManageInternalUsersUrl.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return ManageInternalUsersUrl.UNKNOWN;
	} 
}
