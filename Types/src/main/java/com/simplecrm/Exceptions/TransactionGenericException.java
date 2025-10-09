package com.simplecrm.Exceptions;

public class TransactionGenericException extends RuntimeException {
    public TransactionGenericException(String message) {
        super(message);
    }

    public TransactionGenericException(String message, Throwable cause) {
        super(message, cause);
    }
}