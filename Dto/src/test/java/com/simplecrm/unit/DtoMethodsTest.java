package com.simplecrm.unit;

import com.simplecrm.AnalyticsDTO.BestPeriodResultDTO;
import com.simplecrm.RequestDTO.Seller.SellerCreateRequestDTO;
import com.simplecrm.RequestDTO.Seller.SellerUpdateRequestDTO;
import com.simplecrm.RequestDTO.Transaction.TransactionCreateRequestDTO;
import com.simplecrm.RequestDTO.Transaction.TransactionUpdateRequestDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DtoMethodsTest {

    @Test
    void bestPeriodResultDTOEqualsHashCodeToString() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        BestPeriodResultDTO dto1 = new BestPeriodResultDTO(start, end, 5);
        BestPeriodResultDTO dto2 = new BestPeriodResultDTO(start, end, 5);
        BestPeriodResultDTO dto3 = new BestPeriodResultDTO(start, end, 10);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("transactionCount=5"));
    }

    @Test
    void sellerCreateRequestDTOEqualsHashCodeToString() {
        SellerCreateRequestDTO dto1 = new SellerCreateRequestDTO();
        dto1.setName("Name");
        dto1.setContactInfo("Contact");

        SellerCreateRequestDTO dto2 = new SellerCreateRequestDTO();
        dto2.setName("Name");
        dto2.setContactInfo("Contact");

        SellerCreateRequestDTO dto3 = new SellerCreateRequestDTO();
        dto3.setName("Other");
        dto3.setContactInfo("Contact");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertTrue(dto1.toString().contains("Name"));
    }

    @Test
    void sellerUpdateRequestDTOEqualsHashCodeToString() {
        SellerUpdateRequestDTO dto1 = new SellerUpdateRequestDTO();
        dto1.setName("Name");
        dto1.setContactInfo("Contact");
        dto1.setVersion(1L);

        SellerUpdateRequestDTO dto2 = new SellerUpdateRequestDTO();
        dto2.setName("Name");
        dto2.setContactInfo("Contact");
        dto2.setVersion(1L);

        SellerUpdateRequestDTO dto3 = new SellerUpdateRequestDTO();
        dto3.setName("Name");
        dto3.setContactInfo("Contact");
        dto3.setVersion(2L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("version=1"));
    }

    @Test
    void transactionCreateRequestDTOEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        TransactionCreateRequestDTO dto1 = new TransactionCreateRequestDTO();
        dto1.setSellerId(1L);
        dto1.setAmount(BigDecimal.TEN);
        dto1.setPaymentType(com.simplecrm.Models.Enums.PaymentType.CASH);
        dto1.setTransactionDate(now);

        TransactionCreateRequestDTO dto2 = new TransactionCreateRequestDTO();
        dto2.setSellerId(1L);
        dto2.setAmount(BigDecimal.TEN);
        dto2.setPaymentType(com.simplecrm.Models.Enums.PaymentType.CASH);
        dto2.setTransactionDate(now);

        TransactionCreateRequestDTO dto3 = new TransactionCreateRequestDTO();
        dto3.setSellerId(2L);
        dto3.setAmount(BigDecimal.TEN);
        dto3.setPaymentType(com.simplecrm.Models.Enums.PaymentType.CASH);
        dto3.setTransactionDate(now);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("sellerId=1"));
    }

    @Test
    void transactionUpdateRequestDTOEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        TransactionUpdateRequestDTO dto1 = new TransactionUpdateRequestDTO();
        dto1.setSellerId(1L);
        dto1.setAmount(BigDecimal.TEN);
        dto1.setPaymentType(com.simplecrm.Models.Enums.PaymentType.CASH);
        dto1.setTransactionDate(now);
        dto1.setVersion(1L);

        TransactionUpdateRequestDTO dto2 = new TransactionUpdateRequestDTO();
        dto2.setSellerId(1L);
        dto2.setAmount(BigDecimal.TEN);
        dto2.setPaymentType(com.simplecrm.Models.Enums.PaymentType.CASH);
        dto2.setTransactionDate(now);
        dto2.setVersion(1L);

        TransactionUpdateRequestDTO dto3 = new TransactionUpdateRequestDTO();
        dto3.setSellerId(1L);
        dto3.setAmount(BigDecimal.TEN);
        dto3.setPaymentType(com.simplecrm.Models.Enums.PaymentType.CASH);
        dto3.setTransactionDate(now);
        dto3.setVersion(2L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("version=1"));
    }

    @Test
    void sellerResponseDTOEqualsHashCodeToString() {
        LocalDateTime regDate = LocalDateTime.now();

        SellerResponseDTO dto1 = new SellerResponseDTO();
        dto1.setId(1L);
        dto1.setName("Name");
        dto1.setContactInfo("Contact");
        dto1.setRegistrationDate(regDate);
        dto1.setVersion(1L);

        SellerResponseDTO dto2 = new SellerResponseDTO();
        dto2.setId(1L);
        dto2.setName("Name");
        dto2.setContactInfo("Contact");
        dto2.setRegistrationDate(regDate);
        dto2.setVersion(1L);

        SellerResponseDTO dto3 = new SellerResponseDTO();
        dto3.setId(2L);
        dto3.setName("Name");
        dto3.setContactInfo("Contact");
        dto3.setRegistrationDate(regDate);
        dto3.setVersion(1L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertTrue(dto1.toString().contains("Name"));
    }

    @Test
    void transactionResponseDTOEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        SellerResponseDTO seller = new SellerResponseDTO();
        seller.setId(1L);
        seller.setName("Seller");
        seller.setContactInfo("Contact");

        TransactionResponseDTO dto1 = new TransactionResponseDTO();
        dto1.setId(1L);
        dto1.setSeller(seller);
        dto1.setAmount(BigDecimal.ONE);
        dto1.setPaymentType(com.simplecrm.Models.Enums.PaymentType.CASH);
        dto1.setTransactionDate(now);
        dto1.setVersion(1L);

        TransactionResponseDTO dto2 = new TransactionResponseDTO();
        dto2.setId(1L);
        dto2.setSeller(seller);
        dto2.setAmount(BigDecimal.ONE);
        dto2.setPaymentType(com.simplecrm.Models.Enums.PaymentType.CASH);
        dto2.setTransactionDate(now);
        dto2.setVersion(1L);

        TransactionResponseDTO dto3 = new TransactionResponseDTO();
        dto3.setId(2L);
        dto3.setSeller(seller);
        dto3.setAmount(BigDecimal.ONE);
        dto3.setPaymentType(com.simplecrm.Models.Enums.PaymentType.CASH);
        dto3.setTransactionDate(now);
        dto3.setVersion(1L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertTrue(dto1.toString().contains("id=1"));
    }
}
