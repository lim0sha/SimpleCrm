package com.simplecrm.unit;

import com.simplecrm.ErrorTypes.SellerError;
import com.simplecrm.ErrorTypes.TransactionError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumValueTest {

    @Test
    void sellerError_enumValues() {
        SellerError[] values = SellerError.values();
        assertArrayEquals(new SellerError[]{SellerError.VALIDATION_ERROR, SellerError.NOT_FOUND, SellerError.GENERIC_ERROR}, values);
    }

    @Test
    void transactionError_enumValues() {
        TransactionError[] values = TransactionError.values();
        assertArrayEquals(new TransactionError[]{
                TransactionError.VALIDATION_ERROR,
                TransactionError.NOT_FOUND,
                TransactionError.SELLER_NOT_FOUND,
                TransactionError.GENERIC_ERROR
        }, values);
    }
}
