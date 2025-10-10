package com.simplecrm.ResultTypes;

import com.simplecrm.ErrorTypes.SellerError;
import com.simplecrm.ResponseDTO.SellerResponseDTO;

public sealed interface SellerResult
        permits SellerResult.Success, SellerResult.ValidationError, SellerResult.NotFoundError, SellerResult.GenericError {

    record Success(SellerResponseDTO seller) implements SellerResult {
    }

    record ValidationError(String message) implements SellerResult {
        public SellerError getErrorType() {
            return SellerError.VALIDATION_ERROR;
        }
    }

    record NotFoundError(String message) implements SellerResult {
        public SellerError getErrorType() {
            return SellerError.NOT_FOUND;
        }
    }

    record GenericError(String message) implements SellerResult {
        public SellerError getErrorType() {
            return SellerError.GENERIC_ERROR;
        }
    }

    default String getMessage() {
        return switch (this) {
            case ValidationError ve -> ve.message();
            case NotFoundError nfe -> nfe.message();
            case GenericError ge -> ge.message();
            default -> null;
        };
    }

    default SellerError getErrorType() {
        return switch (this) {
            case ValidationError ve -> ve.getErrorType();
            case NotFoundError nfe -> nfe.getErrorType();
            case GenericError ge -> ge.getErrorType();
            default -> null;
        };
    }
}