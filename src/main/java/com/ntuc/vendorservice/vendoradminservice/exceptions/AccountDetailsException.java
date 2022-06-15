package com.ntuc.vendorservice.vendoradminservice.exceptions;

public class AccountDetailsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AccountDetailsException() {
		super();
	}
	public AccountDetailsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}
	public AccountDetailsException(String message, Throwable cause) {
		super(message, cause); 
	}
	public AccountDetailsException(String message) {
		super(message); 
	}
	public AccountDetailsException(Throwable cause) {
		super(cause); 
	}
	

}
