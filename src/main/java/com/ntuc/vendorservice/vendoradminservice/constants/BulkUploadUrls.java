package com.ntuc.vendorservice.vendoradminservice.constants;

public enum BulkUploadUrls {
	 VENDOR_ACCOUNT_BULKUPLOAD("/fp.docs/fpadmin/bulk/upload/vendors"),
	 INTENALUSER_ACCOUNT_BULKUPLOAD("/fp.docs/fpadmin/bulk/upload/internal"),  
	 UNKNOWN("UNKNOWN");
	
	protected String value;

	BulkUploadUrls(String value) {
		this.value = value;
	}

	public static BulkUploadUrls fromValue(String value) {
		for (BulkUploadUrls type : BulkUploadUrls.values()) {
			if (type.value.equals(value)) {
				return type;
			}
			continue;
		}
		return BulkUploadUrls.UNKNOWN;
	} 
}
