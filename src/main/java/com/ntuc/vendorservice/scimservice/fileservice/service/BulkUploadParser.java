package com.ntuc.vendorservice.scimservice.fileservice.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.ntuc.vendorservice.scimservice.fileservice.exception.BulkFileContentException;
import com.ntuc.vendorservice.scimservice.fileservice.exception.BulkFileFormatException;
import com.ntuc.vendorservice.vendoraccountservice.models.SystemAccount;

public interface BulkUploadParser {
	 
	List<? extends SystemAccount> readAccountsFromFile(InputStream inputStream) throws IOException, BulkFileContentException, BulkFileFormatException;
}
