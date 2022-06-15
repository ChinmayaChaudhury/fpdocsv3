package com.ntuc.vendorservice.foundationcontext.catalog.exceptions;

public class ServiceRepositoryException extends Exception{

    public ServiceRepositoryException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    public ServiceRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceRepositoryException(String message) {
        super(message);
    }
}
