package com.simplecrm.unit;

import com.simplecrm.Exceptions.*;
import com.simplecrm.Handlers.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleSellerValidation_returnsBadRequestWithMessage() {
        String errorMessage = "Invalid seller data";
        SellerValidationException ex = new SellerValidationException(errorMessage);

        ResponseEntity<String> response = handler.handleSellerValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleSellerNotFound_returnsNotFound() {
        SellerNotFoundException ex = new SellerNotFoundException("Seller not found");

        ResponseEntity<String> response = handler.handleSellerNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleSellerGeneric_returnsInternalServerErrorWithMessage() {
        String errorMessage = "Internal server error";
        SellerGenericException ex = new SellerGenericException(errorMessage);

        ResponseEntity<String> response = handler.handleSellerGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleTransactionValidation_returnsBadRequestWithMessage() {
        String errorMessage = "Invalid transaction data";
        TransactionValidationException ex = new TransactionValidationException(errorMessage);

        ResponseEntity<String> response = handler.handleTransactionValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleTransactionNotFound_returnsNotFound() {
        TransactionNotFoundException ex = new TransactionNotFoundException("Transaction not found");

        ResponseEntity<String> response = handler.handleTransactionNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleTransactionSellerNotFound_returnsBadRequestWithMessage() {
        String errorMessage = "Seller not found for transaction";
        TransactionSellerNotFoundException ex = new TransactionSellerNotFoundException(errorMessage);

        ResponseEntity<String> response = handler.handleTransactionSellerNotFound(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleTransactionGeneric_returnsInternalServerErrorWithMessage() {
        String errorMessage = "Transaction processing error";
        TransactionGenericException ex = new TransactionGenericException(errorMessage);

        ResponseEntity<String> response = handler.handleTransactionGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleGeneralException_returnsInternalServerErrorWithMessage() {
        String errorMessage = "Unexpected error occurred";
        Exception ex = new Exception(errorMessage);

        ResponseEntity<String> response = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: " + errorMessage, response.getBody());
    }

    @Test
    void handleGeneralException_withNullMessage_returnsInternalServerError() {
        Exception ex = new Exception();

        ResponseEntity<String> response = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: null", response.getBody());
    }

    @Test
    void handleGeneralException_withEmptyMessage_returnsInternalServerError() {
        Exception ex = new Exception("");

        ResponseEntity<String> response = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: ", response.getBody());
    }

    @Test
    void handleGeneralException_withWhitespaceMessage_returnsInternalServerError() {
        Exception ex = new Exception("   ");

        ResponseEntity<String> response = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred:    ", response.getBody());
    }

    @Test
    void handleSellerValidation_withNullMessage_returnsBadRequest() {
        SellerValidationException ex = new SellerValidationException(null);

        ResponseEntity<String> response = handler.handleSellerValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleSellerValidation_withEmptyMessage_returnsBadRequest() {
        SellerValidationException ex = new SellerValidationException("");

        ResponseEntity<String> response = handler.handleSellerValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void handleTransactionValidation_withNullMessage_returnsBadRequest() {
        TransactionValidationException ex = new TransactionValidationException(null);

        ResponseEntity<String> response = handler.handleTransactionValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleTransactionValidation_withEmptyMessage_returnsBadRequest() {
        TransactionValidationException ex = new TransactionValidationException("");

        ResponseEntity<String> response = handler.handleTransactionValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void handleTransactionSellerNotFound_withNullMessage_returnsBadRequest() {
        TransactionSellerNotFoundException ex = new TransactionSellerNotFoundException(null);

        ResponseEntity<String> response = handler.handleTransactionSellerNotFound(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleTransactionSellerNotFound_withEmptyMessage_returnsBadRequest() {
        TransactionSellerNotFoundException ex = new TransactionSellerNotFoundException("");

        ResponseEntity<String> response = handler.handleTransactionSellerNotFound(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void handleSellerGeneric_withNullMessage_returnsInternalServerError() {
        SellerGenericException ex = new SellerGenericException(null);

        ResponseEntity<String> response = handler.handleSellerGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleSellerGeneric_withEmptyMessage_returnsInternalServerError() {
        SellerGenericException ex = new SellerGenericException("");

        ResponseEntity<String> response = handler.handleSellerGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void handleTransactionGeneric_withNullMessage_returnsInternalServerError() {
        TransactionGenericException ex = new TransactionGenericException(null);

        ResponseEntity<String> response = handler.handleTransactionGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleTransactionGeneric_withEmptyMessage_returnsInternalServerError() {
        TransactionGenericException ex = new TransactionGenericException("");

        ResponseEntity<String> response = handler.handleTransactionGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("", response.getBody());
    }
}