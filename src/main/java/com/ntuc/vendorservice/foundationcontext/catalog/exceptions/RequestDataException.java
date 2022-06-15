package com.ntuc.vendorservice.foundationcontext.catalog.exceptions;

public class RequestDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestDataException() {
		super(); 
	}

	public RequestDataException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}

	public RequestDataException(String message, Throwable cause) {
		super(message, cause); 
	}

	public RequestDataException(String message) {
		super(message); 
	}

	public RequestDataException(Throwable cause) {
		super(cause); 
	}
	
}
