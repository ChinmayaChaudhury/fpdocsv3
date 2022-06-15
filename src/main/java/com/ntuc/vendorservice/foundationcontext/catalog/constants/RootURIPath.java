package com.ntuc.vendorservice.foundationcontext.catalog.constants;

public enum RootURIPath {
	 VENDORS_ADMIN("/fp.docs/vadmin"),
	 VENDORS_ADMIN_PROFILE("/fp.docs/vadmin/profile"),
	 FAIRPRICE_ADMIN("/fp.docs/fpadmin"), 
	 FAIRPRICE_ADMIN_PROFILE("/fp.docs/fpadmin/profile"),
	 UNKNOWN("UNKNOWN");
	
	protected String value;

	RootURIPath(String value) {
		this.value = value;
	}

	public static RootURIPath fromValue(String value) {
		for (RootURIPath type : RootURIPath.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return RootURIPath.UNKNOWN;
	} 
}
