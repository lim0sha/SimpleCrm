package com.simplecrm.unit;

import com.simplecrm.ErrorTypes.SellerError;
import com.simplecrm.Exceptions.SellerGenericException;
import com.simplecrm.Exceptions.SellerNotFoundException;
import com.simplecrm.Exceptions.SellerValidationException;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResultTypes.SellerResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SellerExceptionTest {

    @Test
    void sellerValidationException_constructors() {
        SellerValidationException ex1 = new SellerValidationException("msg");
        assertEquals("msg", ex1.getMessage());

        Throwable cause = new RuntimeException();
        SellerValidationException ex2 = new SellerValidationException("msg", cause);
        assertEquals("msg", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void sellerNotFoundException_constructors() {
        SellerNotFoundException ex1 = new SellerNotFoundException("msg");
        assertEquals("msg", ex1.getMessage());

        Throwable cause = new RuntimeException();
        SellerNotFoundException ex2 = new SellerNotFoundException("msg", cause);
        assertEquals("msg", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void sellerGenericException_constructors() {
        SellerGenericException ex1 = new SellerGenericException("msg");
        assertEquals("msg", ex1.getMessage());

        Throwable cause = new RuntimeException();
        SellerGenericException ex2 = new SellerGenericException("msg", cause);
        assertEquals("msg", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }
}