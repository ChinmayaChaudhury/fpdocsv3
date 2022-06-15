package com.ntuc.vendorservice.vendoradminservice.constants;

public enum ManageVendorAccountUrl {
	 VENDORACCOUNT_QUERY("/fp.docs/vadmin/vendorAccount/q"),
	 VENDORACCOUNT_UPDATE("/fp.docs/vadmin/vendorAccount/u"),
	 VENDORACCOUNT_USERADD("/fp.docs/vadmin/vendorAccount/add"), 
	 VENDORACCOUNT_GROUPS("/fp.docs/vadmin/vendorAccount/groups"),
	 VENDORUSERS_QUERY("/fp.docs/vadmin/vendorAccount/vuser/q"),
	 VENDORUSER_CREATE("/fp.docs/vadmin/vendorAccount/vuser/c"),
	 VENDORUSER_ASSIGN_GROUP("/fp.docs/vadmin/vendorAccount/vuser/role/a"),
	 VENDORUSER_DROP_GROUP("/fp.docs/vadmin/vendorAccount/vuser/role/d"),
	 VENDORUSER_ROLES("/fp.docs/vadmin/vendorAccount/vuser/role/q"),
	 VENDORUSER_UPDATE("/fp.docs/vadmin/vendorAccount/vuser/u"),
	 VENDORUSER_DELETE("/fp.docs/vadmin/vendorAccount/vuser/d"),
	 UNKNOWN("UNKNOWN");
	
	protected String value;

	ManageVendorAccountUrl(String value) {
		this.value = value;
	}

	public static ManageVendorAccountUrl fromValue(String value) {
		for (ManageVendorAccountUrl type : ManageVendorAccountUrl.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return ManageVendorAccountUrl.UNKNOWN;
	} 
}
