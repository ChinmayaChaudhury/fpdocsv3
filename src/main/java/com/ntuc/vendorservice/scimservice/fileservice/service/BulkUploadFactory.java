package com.ntuc.vendorservice.scimservice.fileservice.service;

import com.ntuc.vendorservice.scimservice.fileservice.enums.BulkFileType;
import com.ntuc.vendorservice.scimservice.fileservice.enums.BulkUploadType;

import java.util.ArrayList;

/**
 * Bulk upload Adapter
 * @author I305675
 *
 */
public class BulkUploadFactory {

	private static BulkUploadParser bulkUploadParser = inputStream -> new ArrayList<>();

	public static BulkUploadParser getInstance(BulkFileType bulkFileType, BulkUploadType bulkUploadType) {
		switch (bulkFileType) {
		case XLSX:
			if (bulkUploadType == BulkUploadType.VENDOR_ACCOUNT_UPLOAD) {
				bulkUploadParser = new XLSTVendorAccountBulkUploadParser();
			}
			if (bulkUploadType == BulkUploadType.INTERNAL_ACCOUNT_UPLOAD) {
				bulkUploadParser = new XLSTInternalUserBulkUploadParser();
			} 
			break;
		case CSV:
			if (bulkUploadType == BulkUploadType.VENDOR_ACCOUNT_UPLOAD) {
				bulkUploadParser = new CSVVendorAccountBulkUploadParser();
			}
			if (bulkUploadType == BulkUploadType.INTERNAL_ACCOUNT_UPLOAD) {
				bulkUploadParser = new CSVInternalUserBulkUploadParser();
			} 
			break; 
		default:
			break;
		}
		return bulkUploadParser;
	}

}
