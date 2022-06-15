package com.ntuc.vendorservice.scimservice.fileservice.enums;

public enum BulkFileType {
	XLSX("xlsx"), CSV("csv"),UNKNOWN("unknown");
	String value;
	BulkFileType(String value) {
		this.value = value;
	}

	public static BulkFileType fromValue(String value) {
		for (BulkFileType type : BulkFileType.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return BulkFileType.UNKNOWN;
	} 
}
