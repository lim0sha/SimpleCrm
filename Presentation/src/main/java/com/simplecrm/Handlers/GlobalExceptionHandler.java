package com.simplecrm.Handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.simplecrm.Exceptions.*;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SellerValidationException.class)
    public ResponseEntity<String> handleSellerValidation(SellerValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(SellerNotFoundException.class)
    public ResponseEntity<String> handleSellerNotFound(SellerNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(SellerGenericException.class)
    public ResponseEntity<String> handleSellerGeneric(SellerGenericException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(TransactionValidationException.class)
    public ResponseEntity<String> handleTransactionValidation(TransactionValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<String> handleTransactionNotFound(TransactionNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(TransactionSellerNotFoundException.class)
    public ResponseEntity<String> handleTransactionSellerNotFound(TransactionSellerNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(TransactionGenericException.class)
    public ResponseEntity<String> handleTransactionGeneric(TransactionGenericException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}
