package com.ntuc.vendorservice.vendoradminservice.exceptions;

public class SCIServiceException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public SCIServiceException() {
        super();
    }
    public SCIServiceException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    public SCIServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    public SCIServiceException(String message) {
        super(message);
    }
    public SCIServiceException(Throwable cause) {
        super(cause);
    }
}
