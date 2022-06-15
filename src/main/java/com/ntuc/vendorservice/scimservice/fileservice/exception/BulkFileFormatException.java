package com.ntuc.vendorservice.scimservice.fileservice.exception;

public class BulkFileFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static  final String WRONG_FILE_FORMAT_MESSAGE = "Please ensure the file format provided is correct";

	public BulkFileFormatException() {
		super();
		
	}

	public BulkFileFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public BulkFileFormatException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public BulkFileFormatException(String message) {
		super(message);
		
	}

	public BulkFileFormatException(Throwable cause) {
		super(cause);
		
	}
	
	

}
