package com.simplecrm.Exceptions;

public class SellerGenericException extends RuntimeException {
  public SellerGenericException(String message) {
    super(message);
  }

  public SellerGenericException(String message, Throwable cause) {
    super(message, cause);
  }
}