package com.simplecrm.unit;

import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.Models.Enums.PaymentType;
import com.simplecrm.Projections.TransactionFlatView;
import com.simplecrm.Repositories.SellerRepository;
import com.simplecrm.Repositories.TransactionRepository;
import com.simplecrm.RequestDTO.Transaction.TransactionCreateRequestDTO;
import com.simplecrm.RequestDTO.Transaction.TransactionUpdateRequestDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import com.simplecrm.ResultTypes.TransactionResult;
import com.simplecrm.Services.TransactionServiceImpl;
import com.simplecrm.Utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Seller testSeller;
    private Transaction testTransaction;
    private TransactionResponseDTO testResponseDTO;
    private TransactionCreateRequestDTO createRequestDTO;
    private TransactionUpdateRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {
        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setName("Test Seller");
        testSeller.setContactInfo("test@example.com");
        testSeller.setRegistrationDate(LocalDateTime.now());
        testSeller.setVersion(1L);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setSeller(testSeller);
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setPaymentType(PaymentType.CARD);
        testTransaction.setTransactionDate(LocalDateTime.now());
        testTransaction.setVersion(1L);

        SellerResponseDTO sellerResponseDTO = new SellerResponseDTO();
        sellerResponseDTO.setId(1L);
        sellerResponseDTO.setName("Test Seller");
        sellerResponseDTO.setContactInfo("test@example.com");
        sellerResponseDTO.setRegistrationDate(LocalDateTime.now());
        sellerResponseDTO.setVersion(1L);

        testResponseDTO = new TransactionResponseDTO();
        testResponseDTO.setId(1L);
        testResponseDTO.setSeller(sellerResponseDTO);
        testResponseDTO.setAmount(new BigDecimal("100.00"));
        testResponseDTO.setPaymentType(PaymentType.CARD);
        testResponseDTO.setTransactionDate(LocalDateTime.now());
        testResponseDTO.setVersion(1L);

        createRequestDTO = new TransactionCreateRequestDTO();
        createRequestDTO.setSellerId(1L);
        createRequestDTO.setAmount(new BigDecimal("100.00"));
        createRequestDTO.setPaymentType(PaymentType.CARD);
        createRequestDTO.setTransactionDate(LocalDateTime.now());

        updateRequestDTO = new TransactionUpdateRequestDTO();
        updateRequestDTO.setSellerId(1L);
        updateRequestDTO.setAmount(new BigDecimal("200.00"));
        updateRequestDTO.setPaymentType(PaymentType.CASH);
        updateRequestDTO.setTransactionDate(LocalDateTime.now());
        updateRequestDTO.setVersion(1L);
    }

    @Test
    void createTransaction_success() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(mapper.mapEntityToTransactionResponseDto(any(Transaction.class))).thenReturn(testResponseDTO);

        CompletableFuture<TransactionResult> future = transactionService.createTransaction(createRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.Success.class, result);
        TransactionResult.Success success = (TransactionResult.Success) result;
        assertEquals(testResponseDTO, success.transaction());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_sellerNotFound() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.empty());

        CompletableFuture<TransactionResult> future = transactionService.createTransaction(createRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.SellerNotFoundError.class, result);
        TransactionResult.SellerNotFoundError error = (TransactionResult.SellerNotFoundError) result;
        assertEquals("Seller not found with id: 1", error.message());
    }

    @Test
    void createTransaction_exception() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<TransactionResult> future = transactionService.createTransaction(createRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.GenericError.class, result);
        TransactionResult.GenericError error = (TransactionResult.GenericError) result;
        assertTrue(error.message().contains("Error creating transaction"));
        assertTrue(error.message().contains("DB error"));
    }

    @Test
    void getTransactionById_success() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testTransaction));
        when(mapper.mapEntityToTransactionResponseDto(testTransaction)).thenReturn(testResponseDTO);

        CompletableFuture<TransactionResult> future = transactionService.getTransactionById(1L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.Success.class, result);
        TransactionResult.Success success = (TransactionResult.Success) result;
        assertEquals(testResponseDTO, success.transaction());
    }

    @Test
    void getTransactionById_nullId() throws Exception {
        CompletableFuture<TransactionResult> future = transactionService.getTransactionById(null);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        TransactionResult.ValidationError error = (TransactionResult.ValidationError) result;
        assertEquals("Transaction ID must be positive", error.message());
    }

    @Test
    void getTransactionById_zeroId() throws Exception {
        CompletableFuture<TransactionResult> future = transactionService.getTransactionById(0L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        TransactionResult.ValidationError error = (TransactionResult.ValidationError) result;
        assertEquals("Transaction ID must be positive", error.message());
    }

    @Test
    void getTransactionById_notFound() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.empty());

        CompletableFuture<TransactionResult> future = transactionService.getTransactionById(1L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.NotFoundError.class, result);
        TransactionResult.NotFoundError error = (TransactionResult.NotFoundError) result;
        assertEquals("Transaction not found with id: 1", error.message());
    }

    @Test
    void getAllTransactions_success() throws Exception {
        List<Transaction> transactions = List.of(testTransaction);
        List<TransactionResponseDTO> expectedDtos = List.of(testResponseDTO);

        when(transactionRepository.findAllNotDeleted()).thenReturn(transactions);
        when(mapper.mapEntityToTransactionResponseDto(testTransaction)).thenReturn(testResponseDTO);

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getAllTransactions();
        List<TransactionResponseDTO> result = future.get();

        assertEquals(expectedDtos, result);
    }

    @Test
    void getAllTransactions_exception() throws Exception {
        when(transactionRepository.findAllNotDeleted()).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getAllTransactions();
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void updateTransactionById_success() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testTransaction));
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(mapper.mapEntityToTransactionResponseDto(any(Transaction.class))).thenReturn(testResponseDTO);

        CompletableFuture<TransactionResult> future = transactionService.updateTransactionById(1L, updateRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.Success.class, result);
        TransactionResult.Success success = (TransactionResult.Success) result;
        assertEquals(testResponseDTO, success.transaction());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void updateTransactionById_nullId() throws Exception {
        CompletableFuture<TransactionResult> future = transactionService.updateTransactionById(null, updateRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        TransactionResult.ValidationError error = (TransactionResult.ValidationError) result;
        assertEquals("Transaction ID must be positive", error.message());
    }

    @Test
    void updateTransactionById_zeroId() throws Exception {
        CompletableFuture<TransactionResult> future = transactionService.updateTransactionById(0L, updateRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        TransactionResult.ValidationError error = (TransactionResult.ValidationError) result;
        assertEquals("Transaction ID must be positive", error.message());
    }

    @Test
    void updateTransactionById_notFound() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.empty());

        CompletableFuture<TransactionResult> future = transactionService.updateTransactionById(1L, updateRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.NotFoundError.class, result);
        TransactionResult.NotFoundError error = (TransactionResult.NotFoundError) result;
        assertEquals("Transaction not found with id: 1", error.message());
    }

    @Test
    void updateTransactionById_versionMismatch() throws Exception {
        testTransaction.setVersion(2L);
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testTransaction));

        CompletableFuture<TransactionResult> future = transactionService.updateTransactionById(1L, updateRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        TransactionResult.ValidationError error = (TransactionResult.ValidationError) result;
        assertTrue(error.message().contains("Data is stale"));
        assertTrue(error.message().contains("Expected version: 1"));
        assertTrue(error.message().contains("but found: 2"));
    }

    @Test
    void updateTransactionById_sellerNotFound() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testTransaction));
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.empty());

        CompletableFuture<TransactionResult> future = transactionService.updateTransactionById(1L, updateRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.SellerNotFoundError.class, result);
        TransactionResult.SellerNotFoundError error = (TransactionResult.SellerNotFoundError) result;
        assertEquals("Seller not found with id: 1", error.message());
    }

    @Test
    void updateTransactionById_optimisticLocking() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testTransaction));
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(transactionRepository.save(any(Transaction.class))).thenThrow(new ObjectOptimisticLockingFailureException(Transaction.class, 1L));

        CompletableFuture<TransactionResult> future = transactionService.updateTransactionById(1L, updateRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.GenericError.class, result);
        TransactionResult.GenericError error = (TransactionResult.GenericError) result;
        assertEquals("Concurrent update error. Please try again.", error.message());
    }

    @Test
    void updateTransactionById_exception() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testTransaction));
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(transactionRepository.save(any(Transaction.class))).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<TransactionResult> future = transactionService.updateTransactionById(1L, updateRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.GenericError.class, result);
        TransactionResult.GenericError error = (TransactionResult.GenericError) result;
        assertTrue(error.message().contains("Error updating transaction"));
        assertTrue(error.message().contains("DB error"));
    }

    @Test
    void updateTransactionById_withoutSellerId() throws Exception {
        updateRequestDTO.setSellerId(null);
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(mapper.mapEntityToTransactionResponseDto(any(Transaction.class))).thenReturn(testResponseDTO);

        CompletableFuture<TransactionResult> future = transactionService.updateTransactionById(1L, updateRequestDTO);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.Success.class, result);
        TransactionResult.Success success = (TransactionResult.Success) result;
        assertEquals(testResponseDTO, success.transaction());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deleteTransactionByIdSoft_success() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testTransaction));
        when(mapper.mapEntityToTransactionResponseDto(any(Transaction.class))).thenReturn(testResponseDTO);

        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdSoft(1L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.Success.class, result);
        TransactionResult.Success success = (TransactionResult.Success) result;
        assertEquals(testResponseDTO, success.transaction());
        assertTrue(testTransaction.getDeleted());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void deleteTransactionByIdSoft_nullId() throws Exception {
        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdSoft(null);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        TransactionResult.ValidationError error = (TransactionResult.ValidationError) result;
        assertEquals("Transaction ID must be positive", error.message());
    }

    @Test
    void deleteTransactionByIdSoft_zeroId() throws Exception {
        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdSoft(0L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        TransactionResult.ValidationError error = (TransactionResult.ValidationError) result;
        assertEquals("Transaction ID must be positive", error.message());
    }

    @Test
    void deleteTransactionByIdSoft_notFound() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.empty());

        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdSoft(1L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.NotFoundError.class, result);
        TransactionResult.NotFoundError error = (TransactionResult.NotFoundError) result;
        assertEquals("Transaction not found with id: 1", error.message());
    }

    @Test
    void deleteTransactionByIdSoft_exception() throws Exception {
        when(transactionRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdSoft(1L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.GenericError.class, result);
        TransactionResult.GenericError error = (TransactionResult.GenericError) result;
        assertTrue(error.message().contains("Error deleting transaction"));
        assertTrue(error.message().contains("DB error"));
    }

    @Test
    void deleteTransactionByIdHard_success() throws Exception {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdHard(1L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.Success.class, result);
        TransactionResult.Success success = (TransactionResult.Success) result;
        assertNull(success.transaction());
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void deleteTransactionByIdHard_nullId() throws Exception {
        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdHard(null);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        TransactionResult.ValidationError error = (TransactionResult.ValidationError) result;
        assertEquals("Invalid transaction ID: null", error.message());
    }

    @Test
    void deleteTransactionByIdHard_zeroId() throws Exception {
        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdHard(0L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.ValidationError.class, result);
        TransactionResult.ValidationError error = (TransactionResult.ValidationError) result;
        assertEquals("Invalid transaction ID: 0", error.message());
    }

    @Test
    void deleteTransactionByIdHard_notFound() throws Exception {
        when(transactionRepository.existsById(1L)).thenReturn(false);

        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdHard(1L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.NotFoundError.class, result);
        TransactionResult.NotFoundError error = (TransactionResult.NotFoundError) result;
        assertEquals("Transaction not found with id: 1", error.message());
    }

    @Test
    void deleteTransactionByIdHard_exception() throws Exception {
        when(transactionRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("DB error")).when(transactionRepository).deleteById(1L);

        CompletableFuture<TransactionResult> future = transactionService.deleteTransactionByIdHard(1L);
        TransactionResult result = future.get();

        assertInstanceOf(TransactionResult.GenericError.class, result);
        TransactionResult.GenericError error = (TransactionResult.GenericError) result;
        assertTrue(error.message().contains("Error deleting transaction"));
        assertTrue(error.message().contains("DB error"));
    }

    @Test
    void getTransactionsBySellerId_success() throws Exception {
        TransactionFlatView.SellerView sellerView = new TransactionFlatView.SellerView() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getName() {
                return "Test Seller";
            }

            @Override
            public String getContactInfo() {
                return "test@example.com";
            }

            @Override
            public LocalDateTime getRegistrationDate() {
                return LocalDateTime.now();
            }

            @Override
            public Long getVersion() {
                return 1L;
            }
        };

        TransactionFlatView flatView = new TransactionFlatView() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public BigDecimal getAmount() {
                return new BigDecimal("100.00");
            }

            @Override
            public PaymentType getPaymentType() {
                return PaymentType.CARD;
            }

            @Override
            public LocalDateTime getTransactionDate() {
                return LocalDateTime.now();
            }

            @Override
            public Long getVersion() {
                return 1L;
            }

            @Override
            public SellerView getSeller() {
                return sellerView;
            }
        };

        List<TransactionFlatView> expected = List.of(flatView);

        when(transactionRepository.findFlatBySellerId(1L)).thenReturn(expected);

        CompletableFuture<List<TransactionFlatView>> future = transactionService.getTransactionsBySellerId(1L);
        List<TransactionFlatView> result = future.get();

        assertEquals(expected, result);
    }

    @Test
    void getTransactionsBySellerId_nullId() throws Exception {
        CompletableFuture<List<TransactionFlatView>> future = transactionService.getTransactionsBySellerId(null);
        List<TransactionFlatView> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsBySellerId_zeroId() throws Exception {
        CompletableFuture<List<TransactionFlatView>> future = transactionService.getTransactionsBySellerId(0L);
        List<TransactionFlatView> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsBySellerId_exception() throws Exception {
        when(transactionRepository.findFlatBySellerId(1L)).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<List<TransactionFlatView>> future = transactionService.getTransactionsBySellerId(1L);
        assertThrows(ExecutionException.class, future::get);
    }

    @Test
    void getTransactionsBySellerIdAndDateRange_success() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<Transaction> transactions = List.of(testTransaction);
        List<TransactionResponseDTO> expectedDtos = List.of(testResponseDTO);

        when(transactionRepository.findBySellerIdAndDateRange(1L, start, end)).thenReturn(transactions);
        when(mapper.mapEntityToTransactionResponseDto(testTransaction)).thenReturn(testResponseDTO);

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsBySellerIdAndDateRange(1L, start, end);
        List<TransactionResponseDTO> result = future.get();

        assertEquals(expectedDtos, result);
    }

    @Test
    void getTransactionsBySellerIdAndDateRange_nullSellerId() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsBySellerIdAndDateRange(null, start, end);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsBySellerIdAndDateRange_zeroSellerId() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsBySellerIdAndDateRange(0L, start, end);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsBySellerIdAndDateRange_nullStart() throws Exception {
        LocalDateTime end = LocalDateTime.now();

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsBySellerIdAndDateRange(1L, null, end);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsBySellerIdAndDateRange_nullEnd() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsBySellerIdAndDateRange(1L, start, null);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsBySellerIdAndDateRange_invalidRange() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsBySellerIdAndDateRange(1L, start, end);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsBySellerIdAndDateRange_exception() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(transactionRepository.findBySellerIdAndDateRange(1L, start, end)).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsBySellerIdAndDateRange(1L, start, end);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsByDateRange_success() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<Transaction> transactions = List.of(testTransaction);
        List<TransactionResponseDTO> expectedDtos = List.of(testResponseDTO);

        when(transactionRepository.findByDateRange(start, end)).thenReturn(transactions);
        when(mapper.mapEntityToTransactionResponseDto(testTransaction)).thenReturn(testResponseDTO);

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsByDateRange(start, end);
        List<TransactionResponseDTO> result = future.get();

        assertEquals(expectedDtos, result);
    }

    @Test
    void getTransactionsByDateRange_nullStart() throws Exception {
        LocalDateTime end = LocalDateTime.now();

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsByDateRange(null, end);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsByDateRange_nullEnd() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsByDateRange(start, null);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsByDateRange_invalidRange() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsByDateRange(start, end);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsByDateRange_exception() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(transactionRepository.findByDateRange(start, end)).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<List<TransactionResponseDTO>> future = transactionService.getTransactionsByDateRange(start, end);
        List<TransactionResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }
}