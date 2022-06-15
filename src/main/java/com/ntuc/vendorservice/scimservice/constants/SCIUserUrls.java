package com.ntuc.vendorservice.scimservice.constants;

public enum SCIUserUrls {
	 VENDOR_USER_CREATE("/fp.docs/vadmin/epuser/c"),
	 VENDOR_USER_UPDATE("/fp.docs/vadmin/epuser/u"), 
	 VENDOR_ACCOUNT_CREATE("/fp.docs/fpadmin/epuser/c"),
	 VENDOR_ACCOUNT_UPDATE("/fp.docs/fpadmin/epuser/u"),
	 VENDOR_ACCOUNT_PASSRESET("/fp.docs/fpadmin/epuser/r"),
	 SCI_ACCOUNT_CREATE("/fp.docs/fpadmin/account/c"),
	 SCI_ACCOUNT_DETAILS("/fp.docs/fpadmin/account/q"), 
	 SCI_ACCOUNT_SEARCH("/fp.docs/fpadmin/account/s"), 
	 SCI_ACCOUNT_UPDATE("/fp.docs/fpadmin/account/u"),
	 SCIM_ATTRIBUTES("/fp.docs/scim/attr"), 
	 SCIM_USER_SEARCH("/fp.docs/fpadmin/account/scim"), 
	 SCIM_USER_CREATE("/fp.docs/fpadmin/account/scim/c"),
	 SCIM_USER_DELETE("/fp.docs/fpadmin/account/scim/d"),
	 FPADMIN_SCIM_USER_STATUS("/fp.docs/fpadmin/account/status"),
	 VADMIN_SCIM_USER_STATUS("/fp.docs/vadmin/account/status"),
	 UNKNOWN("UNKNOWN");
	
	protected String value;

	SCIUserUrls(String value) {
		this.value = value;
	}

	public static SCIUserUrls fromValue(String value) {
		for (SCIUserUrls type : SCIUserUrls.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return SCIUserUrls.UNKNOWN;
	} 
}
