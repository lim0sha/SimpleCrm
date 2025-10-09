package com.simplecrm.unit;

import com.simplecrm.AnalyticsDTO.BestPeriodResultDTO;
import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.Models.Enums.PaymentType;
import com.simplecrm.Repositories.SellerRepository;
import com.simplecrm.Repositories.TransactionRepository;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.Services.AnalyticsServiceImpl;
import com.simplecrm.Utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private Seller testSeller;
    private SellerResponseDTO testSellerResponseDTO;
    private Transaction testTransaction1;
    private Transaction testTransaction2;
    private Transaction testTransaction3;

    @BeforeEach
    void setUp() {
        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setName("Test Seller");
        testSeller.setContactInfo("test@example.com");
        testSeller.setRegistrationDate(LocalDateTime.now());
        testSeller.setVersion(1L);

        testSellerResponseDTO = new SellerResponseDTO();
        testSellerResponseDTO.setId(1L);
        testSellerResponseDTO.setName("Test Seller");
        testSellerResponseDTO.setContactInfo("test@example.com");
        testSellerResponseDTO.setRegistrationDate(LocalDateTime.now());
        testSellerResponseDTO.setVersion(1L);

        testTransaction1 = new Transaction();
        testTransaction1.setId(1L);
        testTransaction1.setSeller(testSeller);
        testTransaction1.setAmount(BigDecimal.TEN);
        testTransaction1.setPaymentType(PaymentType.CARD);
        testTransaction1.setTransactionDate(LocalDateTime.now().minusDays(3));
        testTransaction1.setVersion(1L);

        testTransaction2 = new Transaction();
        testTransaction2.setId(2L);
        testTransaction2.setSeller(testSeller);
        testTransaction2.setAmount(BigDecimal.ONE);
        testTransaction2.setPaymentType(PaymentType.CASH);
        testTransaction2.setTransactionDate(LocalDateTime.now().minusDays(2));
        testTransaction2.setVersion(1L);

        testTransaction3 = new Transaction();
        testTransaction3.setId(3L);
        testTransaction3.setSeller(testSeller);
        testTransaction3.setAmount(BigDecimal.ONE);
        testTransaction3.setPaymentType(PaymentType.CASH);
        testTransaction3.setTransactionDate(LocalDateTime.now().minusDays(1));
        testTransaction3.setVersion(1L);
    }

    @Test
    void findTopSellerByPeriod_success() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        List<Seller> sellers = List.of(testSeller);
        List<SellerResponseDTO> expected = List.of(testSellerResponseDTO);

        when(sellerRepository.findTopSellerByPeriod(start, end)).thenReturn(sellers);
        when(mapper.mapEntityToSellerResponseDto(testSeller)).thenReturn(testSellerResponseDTO);

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findTopSellerByPeriod(start, end);
        List<SellerResponseDTO> result = future.get();

        assertEquals(expected, result);
    }

    @Test
    void findTopSellerByPeriod_nullStart() throws Exception {
        LocalDateTime end = LocalDateTime.now();

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findTopSellerByPeriod(null, end);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findTopSellerByPeriod_nullEnd() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(7);

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findTopSellerByPeriod(start, null);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findTopSellerByPeriod_invalidRange() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(7);

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findTopSellerByPeriod(start, end);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findTopSellerByPeriod_exception() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        when(sellerRepository.findTopSellerByPeriod(start, end)).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findTopSellerByPeriod(start, end);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findSellersWithTotalAmountLessThan_success() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(100);
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        List<Seller> sellers = List.of(testSeller);
        List<SellerResponseDTO> expected = List.of(testSellerResponseDTO);

        when(sellerRepository.findSellersWithAmountLessThan(amount, start, end)).thenReturn(sellers);
        when(mapper.mapEntityToSellerResponseDto(testSeller)).thenReturn(testSellerResponseDTO);

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findSellersWithTotalAmountLessThan(amount, start, end);
        List<SellerResponseDTO> result = future.get();

        assertEquals(expected, result);
    }

    @Test
    void findSellersWithTotalAmountLessThan_nullAmount() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findSellersWithTotalAmountLessThan(null, start, end);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findSellersWithTotalAmountLessThan_negativeAmount() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(-100);
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findSellersWithTotalAmountLessThan(amount, start, end);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findSellersWithTotalAmountLessThan_nullStart() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(100);
        LocalDateTime end = LocalDateTime.now();

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findSellersWithTotalAmountLessThan(amount, null, end);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findSellersWithTotalAmountLessThan_nullEnd() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(100);
        LocalDateTime start = LocalDateTime.now().minusDays(7);

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findSellersWithTotalAmountLessThan(amount, start, null);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findSellersWithTotalAmountLessThan_invalidRange() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(100);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(7);

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findSellersWithTotalAmountLessThan(amount, start, end);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findSellersWithTotalAmountLessThan_exception() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(100);
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        when(sellerRepository.findSellersWithAmountLessThan(amount, start, end)).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<List<SellerResponseDTO>> future = analyticsService.findSellersWithTotalAmountLessThan(amount, start, end);
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void findBestTransactionPeriodForSeller_success() throws Exception {
        Long sellerId = 1L;
        List<Transaction> transactions = List.of(testTransaction1, testTransaction2, testTransaction3);
        BestPeriodResultDTO expected = new BestPeriodResultDTO(
                testTransaction1.getTransactionDate(),
                testTransaction3.getTransactionDate(),
                3
        );

        when(transactionRepository.findBySellerIdAndNotDeleted(sellerId)).thenReturn(transactions);

        CompletableFuture<BestPeriodResultDTO> future = analyticsService.findBestTransactionPeriodForSeller(sellerId);
        BestPeriodResultDTO result = future.get();

        assertEquals(expected.getStartDate(), result.getStartDate());
        assertEquals(expected.getEndDate(), result.getEndDate());
        assertEquals(expected.getTransactionCount(), result.getTransactionCount());
    }

    @Test
    void findBestTransactionPeriodForSeller_nullId() throws Exception {
        CompletableFuture<BestPeriodResultDTO> future = analyticsService.findBestTransactionPeriodForSeller(null);
        BestPeriodResultDTO result = future.get();

        assertNull(result.getStartDate());
        assertNull(result.getEndDate());
        assertEquals(0, result.getTransactionCount());
    }

    @Test
    void findBestTransactionPeriodForSeller_zeroId() throws Exception {
        CompletableFuture<BestPeriodResultDTO> future = analyticsService.findBestTransactionPeriodForSeller(0L);
        BestPeriodResultDTO result = future.get();

        assertNull(result.getStartDate());
        assertNull(result.getEndDate());
        assertEquals(0, result.getTransactionCount());
    }

    @Test
    void findBestTransactionPeriodForSeller_negativeId() throws Exception {
        CompletableFuture<BestPeriodResultDTO> future = analyticsService.findBestTransactionPeriodForSeller(-1L);
        BestPeriodResultDTO result = future.get();

        assertNull(result.getStartDate());
        assertNull(result.getEndDate());
        assertEquals(0, result.getTransactionCount());
    }

    @Test
    void findBestTransactionPeriodForSeller_emptyTransactions() throws Exception {
        Long sellerId = 1L;
        List<Transaction> transactions = List.of();

        when(transactionRepository.findBySellerIdAndNotDeleted(sellerId)).thenReturn(transactions);

        CompletableFuture<BestPeriodResultDTO> future = analyticsService.findBestTransactionPeriodForSeller(sellerId);
        BestPeriodResultDTO result = future.get();

        assertNull(result.getStartDate());
        assertNull(result.getEndDate());
        assertEquals(0, result.getTransactionCount());
    }

    @Test
    void findBestTransactionPeriodForSeller_singleTransaction() throws Exception {
        Long sellerId = 1L;
        List<Transaction> transactions = List.of(testTransaction1);
        BestPeriodResultDTO expected = new BestPeriodResultDTO(
                testTransaction1.getTransactionDate(),
                testTransaction1.getTransactionDate(),
                1
        );

        when(transactionRepository.findBySellerIdAndNotDeleted(sellerId)).thenReturn(transactions);

        CompletableFuture<BestPeriodResultDTO> future = analyticsService.findBestTransactionPeriodForSeller(sellerId);
        BestPeriodResultDTO result = future.get();

        assertEquals(expected.getStartDate(), result.getStartDate());
        assertEquals(expected.getEndDate(), result.getEndDate());
        assertEquals(expected.getTransactionCount(), result.getTransactionCount());
    }

    @Test
    void findBestTransactionPeriodForSeller_exception() throws Exception {
        Long sellerId = 1L;

        when(transactionRepository.findBySellerIdAndNotDeleted(sellerId)).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<BestPeriodResultDTO> future = analyticsService.findBestTransactionPeriodForSeller(sellerId);

        assertThrows(ExecutionException.class, future::get);
    }

    @Test
    void findBestTransactionPeriodForSeller_multipleTransactions() throws Exception {
        Long sellerId = 1L;
        LocalDateTime baseTime = LocalDateTime.now();

        Transaction tx1 = new Transaction();
        tx1.setTransactionDate(baseTime.minusDays(5));

        Transaction tx2 = new Transaction();
        tx2.setTransactionDate(baseTime.minusDays(4));

        Transaction tx3 = new Transaction();
        tx3.setTransactionDate(baseTime.minusDays(3));

        Transaction tx4 = new Transaction();
        tx4.setTransactionDate(baseTime.minusDays(1));

        List<Transaction> transactions = List.of(tx1, tx2, tx3, tx4);
        BestPeriodResultDTO expected = new BestPeriodResultDTO(
                tx1.getTransactionDate(),
                tx4.getTransactionDate(),
                4
        );

        when(transactionRepository.findBySellerIdAndNotDeleted(sellerId)).thenReturn(transactions);

        CompletableFuture<BestPeriodResultDTO> future = analyticsService.findBestTransactionPeriodForSeller(sellerId);
        BestPeriodResultDTO result = future.get();

        assertEquals(expected.getStartDate(), result.getStartDate());
        assertEquals(expected.getEndDate(), result.getEndDate());
        assertEquals(expected.getTransactionCount(), result.getTransactionCount());
    }
}