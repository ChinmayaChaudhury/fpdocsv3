package com.ntuc.vendorservice.foundationcontext.catalog.enums;

public enum RecipientType {
	CC("cc"),BCC("bcc"),TO("to"),UNKNOWN("Unknown");
	String value;
	RecipientType(String value) {
		this.value = value;
	}

	public static RecipientType fromValue(String value) {
		for (RecipientType type : RecipientType.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return RecipientType.UNKNOWN;
	} 
	public String getValue() {
		return value;
	}
}
