package com.simplecrm.Exceptions;

public class TransactionSellerNotFoundException extends RuntimeException {
    public TransactionSellerNotFoundException(String message) {
        super(message);
    }

    public TransactionSellerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}