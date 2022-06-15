package com.ntuc.vendorservice.vendoradminservice.exceptions;

import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.ServiceRepositoryException;

public class AccountManagementException extends ServiceRepositoryException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AccountManagementException(String message, Throwable cause, boolean enableSuppression,
									  boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}
	public AccountManagementException(String message, Throwable cause) {
		super(message, cause); 
	}
	public AccountManagementException(String message) {
		super(message); 
	}
	

}
