package com.simplecrm.unit;

import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.Models.Enums.PaymentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ModelEntitiesTest {

    @Test
    void paymentTypeEnumMethods() {
        PaymentType type1 = PaymentType.CASH;
        PaymentType type2 = PaymentType.CASH;
        PaymentType type3 = PaymentType.CARD;

        assertEquals(type1, type2);
        assertNotEquals(type1, type3);
        assertEquals(type1.hashCode(), type2.hashCode());
        assertNotEquals(type1.hashCode(), type3.hashCode());
        assertTrue(type1.toString().contains("CASH"));
    }

    @Test
    void sellerEntityEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        Seller seller1 = new Seller();
        seller1.setId(1L);
        seller1.setName("Seller1");
        seller1.setContactInfo("Contact1");
        seller1.setRegistrationDate(now);
        seller1.setDeleted(false);
        seller1.setVersion(1L);

        Seller seller2 = new Seller();
        seller2.setId(1L);
        seller2.setName("Seller1");
        seller2.setContactInfo("Contact1");
        seller2.setRegistrationDate(now);
        seller2.setDeleted(false);
        seller2.setVersion(1L);

        Seller seller3 = new Seller();
        seller3.setId(2L);
        seller3.setName("Seller2");
        seller3.setContactInfo("Contact2");
        seller3.setRegistrationDate(now);
        seller3.setDeleted(false);
        seller3.setVersion(1L);

        assertEquals(seller1, seller2);
        assertNotEquals(seller1, seller3);
        assertEquals(seller1.hashCode(), seller2.hashCode());
        assertNotEquals(seller1.hashCode(), seller3.hashCode());
        assertTrue(seller1.toString().contains("Seller1"));
    }

    @Test
    void transactionEntityEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("Seller1");
        seller.setContactInfo("Contact1");
        seller.setRegistrationDate(now);
        seller.setDeleted(false);
        seller.setVersion(1L);

        Transaction tx1 = new Transaction();
        tx1.setId(1L);
        tx1.setSeller(seller);
        tx1.setAmount(BigDecimal.TEN);
        tx1.setPaymentType(PaymentType.CASH);
        tx1.setTransactionDate(now);
        tx1.setDeleted(false);
        tx1.setVersion(1L);

        Transaction tx2 = new Transaction();
        tx2.setId(1L);
        tx2.setSeller(seller);
        tx2.setAmount(BigDecimal.TEN);
        tx2.setPaymentType(PaymentType.CASH);
        tx2.setTransactionDate(now);
        tx2.setDeleted(false);
        tx2.setVersion(1L);

        Transaction tx3 = new Transaction();
        tx3.setId(2L);
        tx3.setSeller(seller);
        tx3.setAmount(BigDecimal.ONE);
        tx3.setPaymentType(PaymentType.CARD);
        tx3.setTransactionDate(now);
        tx3.setDeleted(false);
        tx3.setVersion(1L);

        assertEquals(tx1, tx2);
        assertNotEquals(tx1, tx3);
        assertEquals(tx1.hashCode(), tx2.hashCode());
        assertNotEquals(tx1.hashCode(), tx3.hashCode());
        assertTrue(tx1.toString().contains("id=1"));
    }
}
