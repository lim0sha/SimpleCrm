package com.simplecrm.unit;

import com.simplecrm.ErrorTypes.SellerError;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResultTypes.SellerResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumErrorResultTypeTest {

    @Test
    void successResult_shouldContainSeller() {
        SellerResponseDTO seller = new SellerResponseDTO();
        seller.setId(1L);
        seller.setName("Alice");

        SellerResult.Success result = new SellerResult.Success(seller);

        assertInstanceOf(SellerResult.Success.class, result);
        assertEquals(seller, result.seller());
        assertNull(result.getMessage());
        assertNull(result.getErrorType());
    }

    @Test
    void validationError_shouldReturnCorrectMessageAndType() {
        String message = "Invalid data";
        SellerResult result = new SellerResult.ValidationError(message);

        assertInstanceOf(SellerResult.ValidationError.class, result);
        assertEquals(message, result.getMessage());
        assertEquals(SellerError.VALIDATION_ERROR, result.getErrorType());
    }

    @Test
    void notFoundError_shouldReturnCorrectMessageAndType() {
        String message = "Seller not found";
        SellerResult result = new SellerResult.NotFoundError(message);

        assertInstanceOf(SellerResult.NotFoundError.class, result);
        assertEquals(message, result.getMessage());
        assertEquals(SellerError.NOT_FOUND, result.getErrorType());
    }

    @Test
    void genericError_shouldReturnCorrectMessageAndType() {
        String message = "Unexpected error";
        SellerResult result = new SellerResult.GenericError(message);

        assertInstanceOf(SellerResult.GenericError.class, result);
        assertEquals(message, result.getMessage());
        assertEquals(SellerError.GENERIC_ERROR, result.getErrorType());
    }
}
