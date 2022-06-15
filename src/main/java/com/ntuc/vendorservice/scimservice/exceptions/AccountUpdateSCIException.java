package com.ntuc.vendorservice.scimservice.exceptions;

public class AccountUpdateSCIException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AccountUpdateSCIException() {
		super();
	}
	public AccountUpdateSCIException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}
	public AccountUpdateSCIException(String message, Throwable cause) {
		super(message, cause); 
	}
	public AccountUpdateSCIException(String message) {
		super(message); 
	}
	public AccountUpdateSCIException(Throwable cause) {
		super(cause); 
	}
	

}
