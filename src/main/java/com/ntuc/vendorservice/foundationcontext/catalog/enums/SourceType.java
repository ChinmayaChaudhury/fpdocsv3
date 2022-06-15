package com.ntuc.vendorservice.foundationcontext.catalog.enums;

public enum SourceType {

	APPNT_CONF_SRC("Appointment Confirmation"),APPNT_CONF_PATH("PMD/ItemCreation/Item Templates"),UNKNOWN("");
	String value;
	SourceType(String value) {
		this.value = value;
	}

	public static SourceType fromValue(String value) {
		for (SourceType type : SourceType.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return SourceType.UNKNOWN;
	} 
	public String getValue() {
		return value;
	}

}
