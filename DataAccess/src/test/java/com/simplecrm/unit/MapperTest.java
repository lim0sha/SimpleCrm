package com.simplecrm.unit;

import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.Models.Enums.PaymentType;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import com.simplecrm.Utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    private Mapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new Mapper();
    }

    @Test
    void mapEntityToTransactionResponseDto_nullTransaction_returnsNull() {
        assertNull(mapper.mapEntityToTransactionResponseDto(null));
    }

    @Test
    void mapEntityToTransactionResponseDto_withSeller_mapsCorrectly() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("John");
        seller.setContactInfo("john@example.com");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setVersion(5L);

        Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setSeller(seller);
        transaction.setAmount(new BigDecimal("123.45"));
        transaction.setPaymentType(PaymentType.CARD);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setVersion(2L);

        TransactionResponseDTO dto = mapper.mapEntityToTransactionResponseDto(transaction);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals(new BigDecimal("123.45"), dto.getAmount());
        assertEquals(PaymentType.CARD, dto.getPaymentType());
        assertEquals(transaction.getTransactionDate(), dto.getTransactionDate());
        assertEquals(2L, dto.getVersion());

        assertNotNull(dto.getSeller());
        assertEquals(1L, dto.getSeller().getId());
        assertEquals("John", dto.getSeller().getName());
        assertEquals("john@example.com", dto.getSeller().getContactInfo());
        assertEquals(seller.getRegistrationDate(), dto.getSeller().getRegistrationDate());
        assertEquals(5L, dto.getSeller().getVersion());
    }

    @Test
    void mapEntityToTransactionResponseDto_nullSeller_mapsTransactionOnly() {
        Transaction transaction = new Transaction();
        transaction.setId(101L);
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setPaymentType(PaymentType.CASH);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setVersion(1L);

        TransactionResponseDTO dto = mapper.mapEntityToTransactionResponseDto(transaction);

        assertNotNull(dto);
        assertEquals(101L, dto.getId());
        assertEquals(new BigDecimal("50.00"), dto.getAmount());
        assertEquals(PaymentType.CASH, dto.getPaymentType());
        assertEquals(transaction.getTransactionDate(), dto.getTransactionDate());
        assertEquals(1L, dto.getVersion());
        assertNull(dto.getSeller());
    }

    @Test
    void mapSellerEntityToResponseDto_nullSeller_returnsNull() {
        assertNull(mapper.mapSellerEntityToResponseDto(null));
    }

    @Test
    void mapSellerEntityToResponseDto_mapsCorrectly() {
        Seller seller = new Seller();
        seller.setId(2L);
        seller.setName("Alice");
        seller.setContactInfo("alice@example.com");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setVersion(3L);

        SellerResponseDTO dto = mapper.mapSellerEntityToResponseDto(seller);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@example.com", dto.getContactInfo());
        assertEquals(seller.getRegistrationDate(), dto.getRegistrationDate());
        assertEquals(3L, dto.getVersion());
    }

    @Test
    void mapEntityToSellerResponseDto_nullSeller_returnsNull() {
        assertNull(mapper.mapEntityToSellerResponseDto(null));
    }

    @Test
    void mapEntityToSellerResponseDto_mapsCorrectly_withoutVersion() {
        Seller seller = new Seller();
        seller.setId(5L);
        seller.setName("Bob");
        seller.setContactInfo("bob@example.com");
        seller.setRegistrationDate(LocalDateTime.now());

        SellerResponseDTO dto = mapper.mapEntityToSellerResponseDto(seller);

        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("Bob", dto.getName());
        assertEquals("bob@example.com", dto.getContactInfo());
        assertEquals(seller.getRegistrationDate(), dto.getRegistrationDate());
        assertEquals(null, dto.getVersion());
    }
}
