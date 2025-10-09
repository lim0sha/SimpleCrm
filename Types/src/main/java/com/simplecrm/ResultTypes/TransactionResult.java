package com.simplecrm.ResultTypes;

import com.simplecrm.ErrorTypes.TransactionError;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;

public sealed interface TransactionResult
        permits TransactionResult.Success, TransactionResult.ValidationError, TransactionResult.NotFoundError, TransactionResult.SellerNotFoundError, TransactionResult.GenericError {

    record Success(TransactionResponseDTO transaction) implements TransactionResult {
    }

    record ValidationError(String message) implements TransactionResult {
        public TransactionError getErrorType() {
            return TransactionError.VALIDATION_ERROR;
        }
    }

    record NotFoundError(String message) implements TransactionResult {
        public TransactionError getErrorType() {
            return TransactionError.NOT_FOUND;
        }
    }

    record SellerNotFoundError(String message) implements TransactionResult {
        public TransactionError getErrorType() {
            return TransactionError.SELLER_NOT_FOUND;
        }
    }

    record GenericError(String message) implements TransactionResult {
        public TransactionError getErrorType() {
            return TransactionError.GENERIC_ERROR;
        }
    }

    default String getMessage() {
        return switch (this) {
            case ValidationError ve -> ve.message();
            case NotFoundError nfe -> nfe.message();
            case SellerNotFoundError snfe -> snfe.message();
            case GenericError ge -> ge.message();
            default -> null;
        };
    }

    default TransactionError getErrorType() {
        return switch (this) {
            case ValidationError ve -> ve.getErrorType();
            case NotFoundError nfe -> nfe.getErrorType();
            case SellerNotFoundError snfe -> snfe.getErrorType();
            case GenericError ge -> ge.getErrorType();
            default -> null;
        };
    }
}