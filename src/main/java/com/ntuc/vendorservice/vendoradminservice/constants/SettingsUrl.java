package com.ntuc.vendorservice.vendoradminservice.constants;

public enum SettingsUrl {
	 VENDOR_CATEGORY("/fp.docs/fpadmin/settings/vc"),
	 CONFIDENTIALITY_LEVEL("/fp.docs/fpadmin/settings/cl"),
	 FOLDER_STRUCTURE("/fp.docs/fpadmin/settings/fs"),
	 DIRECTORY_RESTRICTIONS("/fp.docs/fpadmin/settings/ds"),
	 USER_GROUPS_TYPES("/fp.docs/fpadmin/settings/usergroup/t"),
	 FPUSER_GROUPS_QUERY("/fp.docs/fpadmin/settings/fpusergroup/q"),
	 FPUSER_GROUPS_CREATE("/fp.docs/fpadmin/settings/fpusergroup/c"),
	 FPUSER_GROUPS_UPDATE("/fp.docs/fpadmin/settings/fpusergroup/u"),
	 VUSER_GROUPS_QUERY("/fp.docs/fpadmin/settings/vusergroup/q"),
	 VUSER_GROUPS_CREATE("/fp.docs/fpadmin/settings/vusergroup/c"), 
	 VUSER_GROUPS_UPDATE("/fp.docs/fpadmin/settings/vusergroup/u"),
	 UNKNOWN("UNKNOWN");
	
	protected String value;

	SettingsUrl(String value) {
		this.value = value;
	}

	public static SettingsUrl fromValue(String value) {
		for (SettingsUrl type : SettingsUrl.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return SettingsUrl.UNKNOWN;
	} 
}
