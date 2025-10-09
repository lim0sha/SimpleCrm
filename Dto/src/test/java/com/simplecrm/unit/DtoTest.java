package com.simplecrm.unit;

import com.simplecrm.Models.Enums.PaymentType;
import com.simplecrm.RequestDTO.Transaction.TransactionCreateRequestDTO;
import com.simplecrm.RequestDTO.Transaction.TransactionUpdateRequestDTO;
import com.simplecrm.RequestDTO.Seller.SellerCreateRequestDTO;
import com.simplecrm.RequestDTO.Seller.SellerUpdateRequestDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import com.simplecrm.AnalyticsDTO.BestPeriodResultDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void bestPeriodResultDTOTest() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BestPeriodResultDTO dto = new BestPeriodResultDTO(start, end, 5);
        assertEquals(start, dto.getStartDate());
        assertEquals(end, dto.getEndDate());
        assertEquals(5, dto.getTransactionCount());

        dto.setStartDate(end);
        dto.setEndDate(start);
        dto.setTransactionCount(10);
        assertEquals(end, dto.getStartDate());
        assertEquals(start, dto.getEndDate());
        assertEquals(10, dto.getTransactionCount());

        BestPeriodResultDTO empty = new BestPeriodResultDTO();
        empty.setStartDate(start);
        empty.setEndDate(end);
        empty.setTransactionCount(7);
        assertEquals(start, empty.getStartDate());
        assertEquals(end, empty.getEndDate());
        assertEquals(7, empty.getTransactionCount());
    }

    @Test
    void sellerCreateRequestDTOTest() {
        SellerCreateRequestDTO dto = new SellerCreateRequestDTO();
        dto.setName("Example");
        dto.setContactInfo("john@example.com");
        assertEquals("Example", dto.getName());
        assertEquals("john@example.com", dto.getContactInfo());
    }

    @Test
    void sellerUpdateRequestDTOTest() {
        SellerUpdateRequestDTO dto = new SellerUpdateRequestDTO();
        dto.setName("lim0sha");
        dto.setContactInfo("alice@example.com");
        dto.setVersion(2L);
        assertEquals("lim0sha", dto.getName());
        assertEquals("alice@example.com", dto.getContactInfo());
        assertEquals(2L, dto.getVersion());
    }

    @Test
    void transactionCreateRequestDTOTest() {
        TransactionCreateRequestDTO dto = new TransactionCreateRequestDTO();
        dto.setSellerId(1L);
        dto.setAmount(BigDecimal.valueOf(123.45));
        dto.setPaymentType(PaymentType.CASH);
        LocalDateTime now = LocalDateTime.now();
        dto.setTransactionDate(now);

        assertEquals(1L, dto.getSellerId());
        assertEquals(BigDecimal.valueOf(123.45), dto.getAmount());
        assertEquals(PaymentType.CASH, dto.getPaymentType());
        assertEquals(now, dto.getTransactionDate());
    }

    @Test
    void transactionUpdateRequestDTOTest() {
        TransactionUpdateRequestDTO dto = new TransactionUpdateRequestDTO();
        dto.setSellerId(2L);
        dto.setAmount(BigDecimal.valueOf(543.21));
        dto.setPaymentType(PaymentType.CARD);
        LocalDateTime now = LocalDateTime.now();
        dto.setTransactionDate(now);
        dto.setVersion(3L);

        assertEquals(2L, dto.getSellerId());
        assertEquals(BigDecimal.valueOf(543.21), dto.getAmount());
        assertEquals(PaymentType.CARD, dto.getPaymentType());
        assertEquals(now, dto.getTransactionDate());
        assertEquals(3L, dto.getVersion());
    }

    @Test
    void sellerResponseDTOTest() {
        SellerResponseDTO dto = new SellerResponseDTO();
        dto.setId(1L);
        dto.setName("Seller1");
        dto.setContactInfo("contact@example.com");
        LocalDateTime regDate = LocalDateTime.now();
        dto.setRegistrationDate(regDate);
        dto.setVersion(5L);

        assertEquals(1L, dto.getId());
        assertEquals("Seller1", dto.getName());
        assertEquals("contact@example.com", dto.getContactInfo());
        assertEquals(regDate, dto.getRegistrationDate());
        assertEquals(5L, dto.getVersion());
    }

    @Test
    void transactionResponseDTOTest() {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(10L);
        SellerResponseDTO seller = new SellerResponseDTO();
        seller.setId(1L);
        seller.setName("Seller2");
        seller.setContactInfo("seller1@example.com");
        dto.setSeller(seller);
        dto.setAmount(BigDecimal.valueOf(999.99));
        dto.setPaymentType(PaymentType.TRANSFER);
        LocalDateTime now = LocalDateTime.now();
        dto.setTransactionDate(now);
        dto.setVersion(7L);

        assertEquals(10L, dto.getId());
        assertEquals(seller, dto.getSeller());
        assertEquals(BigDecimal.valueOf(999.99), dto.getAmount());
        assertEquals(PaymentType.TRANSFER, dto.getPaymentType());
        assertEquals(now, dto.getTransactionDate());
        assertEquals(7L, dto.getVersion());
    }
}
