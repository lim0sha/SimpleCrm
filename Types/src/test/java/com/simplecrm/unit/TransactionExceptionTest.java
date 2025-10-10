package com.simplecrm.unit;

import com.simplecrm.Exceptions.TransactionGenericException;
import com.simplecrm.Exceptions.TransactionNotFoundException;
import com.simplecrm.Exceptions.TransactionSellerNotFoundException;
import com.simplecrm.Exceptions.TransactionValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionExceptionTest {

    @Test
    void transactionValidationException_constructors() {
        TransactionValidationException ex1 = new TransactionValidationException("msg");
        assertEquals("msg", ex1.getMessage());

        Throwable cause = new RuntimeException();
        TransactionValidationException ex2 = new TransactionValidationException("msg", cause);
        assertEquals("msg", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void transactionNotFoundException_constructors() {
        TransactionNotFoundException ex1 = new TransactionNotFoundException("msg");
        assertEquals("msg", ex1.getMessage());

        Throwable cause = new RuntimeException();
        TransactionNotFoundException ex2 = new TransactionNotFoundException("msg", cause);
        assertEquals("msg", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void transactionSellerNotFoundException_constructors() {
        TransactionSellerNotFoundException ex1 = new TransactionSellerNotFoundException("msg");
        assertEquals("msg", ex1.getMessage());

        Throwable cause = new RuntimeException();
        TransactionSellerNotFoundException ex2 = new TransactionSellerNotFoundException("msg", cause);
        assertEquals("msg", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void transactionGenericException_constructors() {
        TransactionGenericException ex1 = new TransactionGenericException("msg");
        assertEquals("msg", ex1.getMessage());

        Throwable cause = new RuntimeException();
        TransactionGenericException ex2 = new TransactionGenericException("msg", cause);
        assertEquals("msg", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }
}
