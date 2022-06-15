package com.ntuc.vendorservice.vendoradminservice.constants;

public enum CWFUserManagementPath {
	CREATE_USER("/fp.docs/um/user/c"),
	UPDATE_USER("/fp.docs/um/user/u"),
	QUERY_USER("/fp.docs/um/user/q"), 
	UNKNOWN("UNKNOWN"); 
	protected String value;

	CWFUserManagementPath(String value) {
		this.value = value;
	}

	public static CWFUserManagementPath fromValue(String value) {
		for (CWFUserManagementPath type : CWFUserManagementPath.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return CWFUserManagementPath.UNKNOWN;
	} 
}
