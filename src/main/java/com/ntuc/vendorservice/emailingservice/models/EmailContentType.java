package com.ntuc.vendorservice.emailingservice.models;

public enum EmailContentType {  
	HTML("text/html"), PLAIN("text/plain"),UNKNOWN("unknown");
	String value;
	EmailContentType(String value) {
		this.value = value;
	}

	public static EmailContentType fromValue(String value) {
		for (EmailContentType type : EmailContentType.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return EmailContentType.UNKNOWN;
	} 
	public String getValue() {
		return value;
	}
}
