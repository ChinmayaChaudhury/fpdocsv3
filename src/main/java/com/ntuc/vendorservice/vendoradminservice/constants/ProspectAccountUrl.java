package com.ntuc.vendorservice.vendoradminservice.constants;

public enum ProspectAccountUrl {
	ACCOUNT_CREATE("/fp.docs/prospect/acc/c"), 
	ACCOUNT_QUERY("/fp.docs/prospect/acc/q"),
	ACCOUNT_UPDATE("/fp.docs/prospect/acc/u"),  
	GROUP_CREATE("/fp.docs/prospect/group/c"), 
	GROUP_UPDATE("/fp.docs/prospect/group/u"), 
	GROUP_QUERY("/fp.docs/prospect/group/q"), 
	UNKNOWN("UNKNOWN"),
	ACCOUNT_ACTIVATE("/fp.docs/prospect/acc/a"),
	ACCOUNT_DEACTIVATE("/fp.docs/prospect/acc/d"); 
	protected String value;

	ProspectAccountUrl(String value) {
		this.value = value;
	}

	public static ProspectAccountUrl fromValue(String value) {
		for (ProspectAccountUrl type : ProspectAccountUrl.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return ProspectAccountUrl.UNKNOWN;
	} 
}
