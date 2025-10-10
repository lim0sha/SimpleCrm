package com.simplecrm.unit;

import com.simplecrm.ErrorTypes.TransactionError;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import com.simplecrm.ResultTypes.TransactionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionResultTest {

    @Test
    void successResult_shouldContainTransaction() {
        TransactionResponseDTO transaction = new TransactionResponseDTO();
        transaction.setId(1L);
        transaction.setAmount(null);

        TransactionResult.Success result = new TransactionResult.Success(transaction);

        assertInstanceOf(TransactionResult.Success.class, result);
        assertEquals(transaction, result.transaction());
        assertNull(result.getMessage());
        assertNull(result.getErrorType());
    }

    @Test
    void validationError_shouldReturnCorrectMessageAndType() {
        String message = "Invalid transaction";
        TransactionResult result = new TransactionResult.ValidationError(message);

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        assertEquals(message, result.getMessage());
        assertEquals(TransactionError.VALIDATION_ERROR, result.getErrorType());
    }

    @Test
    void notFoundError_shouldReturnCorrectMessageAndType() {
        String message = "Transaction not found";
        TransactionResult result = new TransactionResult.NotFoundError(message);

        assertInstanceOf(TransactionResult.NotFoundError.class, result);
        assertEquals(message, result.getMessage());
        assertEquals(TransactionError.NOT_FOUND, result.getErrorType());
    }

    @Test
    void sellerNotFoundError_shouldReturnCorrectMessageAndType() {
        String message = "Seller not found for transaction";
        TransactionResult result = new TransactionResult.SellerNotFoundError(message);

        assertInstanceOf(TransactionResult.SellerNotFoundError.class, result);
        assertEquals(message, result.getMessage());
        assertEquals(TransactionError.SELLER_NOT_FOUND, result.getErrorType());
    }

    @Test
    void genericError_shouldReturnCorrectMessageAndType() {
        String message = "Unexpected error";
        TransactionResult result = new TransactionResult.GenericError(message);

        assertInstanceOf(TransactionResult.GenericError.class, result);
        assertEquals(message, result.getMessage());
        assertEquals(TransactionError.GENERIC_ERROR, result.getErrorType());
    }

    @Test
    void transactionResult_methods() {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        TransactionResult.Success success = new TransactionResult.Success(dto);
        assertEquals(dto, success.transaction());
        assertNull(success.getMessage());
        assertNull(success.getErrorType());

        TransactionResult.ValidationError validation = new TransactionResult.ValidationError("msg");
        assertEquals("msg", validation.getMessage());
        assertEquals(TransactionError.VALIDATION_ERROR, validation.getErrorType());

        TransactionResult.NotFoundError notFound = new TransactionResult.NotFoundError("msg");
        assertEquals("msg", notFound.getMessage());
        assertEquals(TransactionError.NOT_FOUND, notFound.getErrorType());

        TransactionResult.SellerNotFoundError sellerNotFound = new TransactionResult.SellerNotFoundError("msg");
        assertEquals("msg", sellerNotFound.getMessage());
        assertEquals(TransactionError.SELLER_NOT_FOUND, sellerNotFound.getErrorType());

        TransactionResult.GenericError generic = new TransactionResult.GenericError("msg");
        assertEquals("msg", generic.getMessage());
        assertEquals(TransactionError.GENERIC_ERROR, generic.getErrorType());
    }

    @Test
    void transactionResult_getErrorType_switchBranches() {
        TransactionResult validation = new TransactionResult.ValidationError("validation error");
        assertEquals(TransactionError.VALIDATION_ERROR, validation.getErrorType());

        TransactionResult notFound = new TransactionResult.NotFoundError("not found error");
        assertEquals(TransactionError.NOT_FOUND, notFound.getErrorType());

        TransactionResult sellerNotFound = new TransactionResult.SellerNotFoundError("seller not found");
        assertEquals(TransactionError.SELLER_NOT_FOUND, sellerNotFound.getErrorType());

        TransactionResult generic = new TransactionResult.GenericError("generic error");
        assertEquals(TransactionError.GENERIC_ERROR, generic.getErrorType());
    }

    @Test
    void transactionResult_getErrorType() {
        TransactionResult validation = new TransactionResult.ValidationError("validation error");
        TransactionError error1 = switch (validation) {
            case TransactionResult.ValidationError ve -> ve.getErrorType();
            case TransactionResult.NotFoundError nfe -> nfe.getErrorType();
            case TransactionResult.SellerNotFoundError snfe -> snfe.getErrorType();
            case TransactionResult.GenericError ge -> ge.getErrorType();
            case TransactionResult.Success success -> null;
        };
        assertEquals(TransactionError.VALIDATION_ERROR, error1);

        TransactionResult notFound = new TransactionResult.NotFoundError("not found error");
        TransactionError error2 = switch (notFound) {
            case TransactionResult.ValidationError ve -> ve.getErrorType();
            case TransactionResult.NotFoundError nfe -> nfe.getErrorType();
            case TransactionResult.SellerNotFoundError snfe -> snfe.getErrorType();
            case TransactionResult.GenericError ge -> ge.getErrorType();
            case TransactionResult.Success success -> null;
        };
        assertEquals(TransactionError.NOT_FOUND, error2);

        TransactionResult sellerNotFound = new TransactionResult.SellerNotFoundError("seller not found");
        TransactionError error3 = switch (sellerNotFound) {
            case TransactionResult.ValidationError ve -> ve.getErrorType();
            case TransactionResult.NotFoundError nfe -> nfe.getErrorType();
            case TransactionResult.SellerNotFoundError snfe -> snfe.getErrorType();
            case TransactionResult.GenericError ge -> ge.getErrorType();
            case TransactionResult.Success success -> null;
        };
        assertEquals(TransactionError.SELLER_NOT_FOUND, error3);

        TransactionResult generic = new TransactionResult.GenericError("generic error");
        TransactionError error4 = switch (generic) {
            case TransactionResult.ValidationError ve -> ve.getErrorType();
            case TransactionResult.NotFoundError nfe -> nfe.getErrorType();
            case TransactionResult.SellerNotFoundError snfe -> snfe.getErrorType();
            case TransactionResult.GenericError ge -> ge.getErrorType();
            case TransactionResult.Success success -> null;
        };
        assertEquals(TransactionError.GENERIC_ERROR, error4);
    }
}
