package com.simplecrm.unit;

import com.simplecrm.ErrorTypes.SellerError;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResultTypes.SellerResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SellerResultTest {

    @Test
    void sellerResult_methods() {
        SellerResponseDTO dto = new SellerResponseDTO();
        SellerResult.Success success = new SellerResult.Success(dto);
        assertEquals(dto, success.seller());
        assertNull(success.getMessage());
        assertNull(success.getErrorType());

        SellerResult.ValidationError validation = new SellerResult.ValidationError("msg");
        assertEquals("msg", validation.getMessage());
        assertEquals(SellerError.VALIDATION_ERROR, validation.getErrorType());

        SellerResult.NotFoundError notFound = new SellerResult.NotFoundError("msg");
        assertEquals("msg", notFound.getMessage());
        assertEquals(SellerError.NOT_FOUND, notFound.getErrorType());

        SellerResult.GenericError generic = new SellerResult.GenericError("msg");
        assertEquals("msg", generic.getMessage());
    }

    @Test
    void sellerResult_getErrorType_switchBranches() {
        SellerResult validation = new SellerResult.ValidationError("validation error");
        assertEquals(SellerError.VALIDATION_ERROR, validation.getErrorType());

        SellerResult notFound = new SellerResult.NotFoundError("not found error");
        assertEquals(SellerError.NOT_FOUND, notFound.getErrorType());

        SellerResult generic = new SellerResult.GenericError("generic error");
        assertEquals(SellerError.GENERIC_ERROR, generic.getErrorType());
    }

    @Test
    void sellerResult_getErrorType() {
        SellerResult validation = new SellerResult.ValidationError("validation error");
        SellerError error1 = switch (validation) {
            case SellerResult.ValidationError ve -> ve.getErrorType();
            case SellerResult.NotFoundError nfe -> nfe.getErrorType();
            case SellerResult.GenericError ge -> ge.getErrorType();
            case SellerResult.Success success -> null;
        };
        assertEquals(SellerError.VALIDATION_ERROR, error1);

        SellerResult notFound = new SellerResult.NotFoundError("not found error");
        SellerError error2 = switch (notFound) {
            case SellerResult.ValidationError ve -> ve.getErrorType();
            case SellerResult.NotFoundError nfe -> nfe.getErrorType();
            case SellerResult.GenericError ge -> ge.getErrorType();
            case SellerResult.Success success -> null;
        };
        assertEquals(SellerError.NOT_FOUND, error2);

        SellerResult generic = new SellerResult.GenericError("generic error");
        SellerError error3 = switch (generic) {
            case SellerResult.ValidationError ve -> ve.getErrorType();
            case SellerResult.NotFoundError nfe -> nfe.getErrorType();
            case SellerResult.GenericError ge -> ge.getErrorType();
            case SellerResult.Success success -> null;
        };
        assertEquals(SellerError.GENERIC_ERROR, error3);
    }
}
