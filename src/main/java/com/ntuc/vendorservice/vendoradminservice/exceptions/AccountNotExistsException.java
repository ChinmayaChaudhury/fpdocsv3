package com.ntuc.vendorservice.vendoradminservice.exceptions;

public class AccountNotExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AccountNotExistsException() {
		super();
	}
	public AccountNotExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}
	public AccountNotExistsException(String message, Throwable cause) {
		super(message, cause); 
	}
	public AccountNotExistsException(String message) {
		super(message); 
	}
	public AccountNotExistsException(Throwable cause) {
		super(cause); 
	}
	

}
