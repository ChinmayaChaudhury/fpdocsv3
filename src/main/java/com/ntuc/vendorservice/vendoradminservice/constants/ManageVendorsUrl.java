package com.ntuc.vendorservice.vendoradminservice.constants;

public enum ManageVendorsUrl {
	 VENDORS_QUERY("/fp.docs/fpadmin/fvendors/q"),
	 VENDORS_SEND_EMAIL("/fp.docs/fpadmin/fvendors/email"),
	 VENDOR_ATTRIBUTE("/fp.docs/fpadmin/fvendors/q/admin"),
	 VENDOR_CREATE("/fp.docs/fpadmin/fvendors/c"),
	 VENDOR_UPDATE("/fp.docs/fpadmin/fvendors/u"),
	 VENDOR_DELETE("/fp.docs/fpadmin/fvendors/d"),
	 FPUSER_VENDORS_QUERY("/fp.docs/fpuser/fpvendors/q"), 
	 ALLOW_VENDORADMIN_DOCUMENTSHARE("/fp.docs/fpadmin/fvendors/admin"),
	 UNKNOWN("UNKNOWN");
	
	protected String value;

	ManageVendorsUrl(String value) {
		this.value = value;
	}

	public static ManageVendorsUrl fromValue(String value) {
		for (ManageVendorsUrl type : ManageVendorsUrl.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return ManageVendorsUrl.UNKNOWN;
	} 
}
