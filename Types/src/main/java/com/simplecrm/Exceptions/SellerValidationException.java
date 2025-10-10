package com.simplecrm.Exceptions;

public class SellerValidationException extends RuntimeException {
    public SellerValidationException(String message) {
        super(message);
    }

    public SellerValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}