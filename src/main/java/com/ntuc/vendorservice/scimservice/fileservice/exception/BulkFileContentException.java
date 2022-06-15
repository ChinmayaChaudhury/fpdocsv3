package com.ntuc.vendorservice.scimservice.fileservice.exception;

public class BulkFileContentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String VENDOR_CATEGORY_MESSAGE = "Value passed for enum not known. Expects any of the three values; M,L,S";
	public static final String EMAIL_MESSAGE = "Email '%s' isn't valid";
	public static final String CELL_FORMAT_MESSAGE = "Cell '%s' Format is wrong, Please update cell to Text";
	public static final String DUPLICATE_VENDORCODE_CONTENT_MESSAGE = "Duplicate entries found, please ensure the vendorcode '%s' has not been duplicated";
	public static final String DUPLICATE_EMAIL_CONTENT_MESSAGE = "Duplicate entries found, please ensure the email '%s' has not been duplicated";

	public BulkFileContentException() {
		super();

	}

	public BulkFileContentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public BulkFileContentException(String message, Throwable cause) {
		super(message, cause);

	}

	public BulkFileContentException(String message) {
		super(message);

	}

	public BulkFileContentException(Throwable cause) {
		super(cause);

	}

}
