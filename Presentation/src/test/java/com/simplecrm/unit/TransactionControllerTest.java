package com.simplecrm.unit;

import com.simplecrm.Controllers.TransactionController;
import com.simplecrm.Projections.TransactionFlatView;
import com.simplecrm.RequestDTO.Transaction.TransactionCreateRequestDTO;
import com.simplecrm.RequestDTO.Transaction.TransactionUpdateRequestDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import com.simplecrm.ResultTypes.TransactionResult;
import com.simplecrm.Services.Interfaces.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTransactions_Success() throws Exception {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        when(transactionService.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(List.of(dto)));

        ResponseEntity<List<TransactionResponseDTO>> response = transactionController.getAllTransactions().get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }


    @Test
    void testGetAllTransactions_Exceptionally() throws Exception {
        CompletableFuture<List<TransactionResponseDTO>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("error"));
        when(transactionService.getAllTransactions()).thenReturn(future);

        ResponseEntity<List<TransactionResponseDTO>> response = transactionController.getAllTransactions().get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetTransactionById_Success() throws Exception {
        TransactionResult.Success success = mock(TransactionResult.Success.class);
        when(transactionService.getTransactionById(1L)).thenReturn(CompletableFuture.completedFuture(success));

        ResponseEntity<TransactionResult> response = transactionController.getTransactionById(1L).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(success, response.getBody());
    }

    @Test
    void testGetTransactionById_NotFound() throws Exception {
        TransactionResult.NotFoundError notFound = new TransactionResult.NotFoundError("Not found");
        when(transactionService.getTransactionById(1L)).thenReturn(CompletableFuture.completedFuture(notFound));

        ResponseEntity<TransactionResult> response = transactionController.getTransactionById(1L).get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(notFound, response.getBody());
    }

    @Test
    void testGetTransactionById_ValidationError() throws Exception {
        TransactionResult.ValidationError validation = new TransactionResult.ValidationError("Invalid");
        when(transactionService.getTransactionById(1L)).thenReturn(CompletableFuture.completedFuture(validation));

        ResponseEntity<TransactionResult> response = transactionController.getTransactionById(1L).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(validation, response.getBody());
    }

    @Test
    void testGetTransactionById_GenericError() throws Exception {
        TransactionResult.GenericError generic = new TransactionResult.GenericError("Generic");
        when(transactionService.getTransactionById(1L)).thenReturn(CompletableFuture.completedFuture(generic));

        ResponseEntity<TransactionResult> response = transactionController.getTransactionById(1L).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(generic, response.getBody());
    }

    @Test
    void testGetTransactionById_Exceptionally() throws Exception {
        CompletableFuture<TransactionResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Unexpected"));
        when(transactionService.getTransactionById(1L)).thenReturn(future);

        ResponseEntity<TransactionResult> response = transactionController.getTransactionById(1L).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(TransactionResult.GenericError.class, response.getBody());
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        TransactionCreateRequestDTO dto = new TransactionCreateRequestDTO();
        TransactionResult.Success success = mock(TransactionResult.Success.class);
        when(transactionService.createTransaction(dto)).thenReturn(CompletableFuture.completedFuture(success));

        ResponseEntity<TransactionResult> response = transactionController.createTransaction(dto).get();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(success, response.getBody());
    }

    @Test
    void testCreateTransaction_ValidationError() throws Exception {
        TransactionCreateRequestDTO dto = new TransactionCreateRequestDTO();
        TransactionResult.ValidationError validation = new TransactionResult.ValidationError("Invalid");
        when(transactionService.createTransaction(dto)).thenReturn(CompletableFuture.completedFuture(validation));

        ResponseEntity<TransactionResult> response = transactionController.createTransaction(dto).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(validation, response.getBody());
    }

    @Test
    void testCreateTransaction_SellerNotFound() throws Exception {
        TransactionCreateRequestDTO dto = new TransactionCreateRequestDTO();
        TransactionResult.SellerNotFoundError snf = new TransactionResult.SellerNotFoundError("Seller missing");
        when(transactionService.createTransaction(dto)).thenReturn(CompletableFuture.completedFuture(snf));

        ResponseEntity<TransactionResult> response = transactionController.createTransaction(dto).get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(snf, response.getBody());
    }

    @Test
    void testCreateTransaction_GenericError() throws Exception {
        TransactionCreateRequestDTO dto = new TransactionCreateRequestDTO();
        TransactionResult.GenericError generic = new TransactionResult.GenericError("Generic");
        when(transactionService.createTransaction(dto)).thenReturn(CompletableFuture.completedFuture(generic));

        ResponseEntity<TransactionResult> response = transactionController.createTransaction(dto).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(generic, response.getBody());
    }

    @Test
    void testCreateTransaction_Exceptionally() throws Exception {
        TransactionCreateRequestDTO dto = new TransactionCreateRequestDTO();
        CompletableFuture<TransactionResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Unexpected"));
        when(transactionService.createTransaction(dto)).thenReturn(future);

        ResponseEntity<TransactionResult> response = transactionController.createTransaction(dto).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(TransactionResult.GenericError.class, response.getBody());
    }

    @Test
    void testUpdateTransaction_Success() throws Exception {
        TransactionUpdateRequestDTO dto = new TransactionUpdateRequestDTO();
        TransactionResult.Success success = mock(TransactionResult.Success.class);
        when(transactionService.updateTransactionById(1L, dto)).thenReturn(CompletableFuture.completedFuture(success));

        ResponseEntity<TransactionResult> response = transactionController.updateTransaction(1L, dto).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(success, response.getBody());
    }

    @Test
    void testUpdateTransaction_NotFound() throws Exception {
        TransactionUpdateRequestDTO dto = new TransactionUpdateRequestDTO();
        TransactionResult.NotFoundError notFound = new TransactionResult.NotFoundError("Missing");
        when(transactionService.updateTransactionById(1L, dto)).thenReturn(CompletableFuture.completedFuture(notFound));

        ResponseEntity<TransactionResult> response = transactionController.updateTransaction(1L, dto).get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(notFound, response.getBody());
    }

    @Test
    void testUpdateTransaction_ValidationError() throws Exception {
        TransactionUpdateRequestDTO dto = new TransactionUpdateRequestDTO();
        TransactionResult.ValidationError validation = new TransactionResult.ValidationError("Invalid");
        when(transactionService.updateTransactionById(1L, dto)).thenReturn(CompletableFuture.completedFuture(validation));

        ResponseEntity<TransactionResult> response = transactionController.updateTransaction(1L, dto).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(validation, response.getBody());
    }

    @Test
    void testUpdateTransaction_GenericError() throws Exception {
        TransactionUpdateRequestDTO dto = new TransactionUpdateRequestDTO();
        TransactionResult.GenericError generic = new TransactionResult.GenericError("Generic");
        when(transactionService.updateTransactionById(1L, dto)).thenReturn(CompletableFuture.completedFuture(generic));

        ResponseEntity<TransactionResult> response = transactionController.updateTransaction(1L, dto).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(generic, response.getBody());
    }

    @Test
    void testUpdateTransaction_Exceptionally() throws Exception {
        TransactionUpdateRequestDTO dto = new TransactionUpdateRequestDTO();
        CompletableFuture<TransactionResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Unexpected"));
        when(transactionService.updateTransactionById(1L, dto)).thenReturn(future);

        ResponseEntity<TransactionResult> response = transactionController.updateTransaction(1L, dto).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(TransactionResult.GenericError.class, response.getBody());
    }

    @Test
    void testDeleteTransaction_Soft_Success() throws Exception {
        TransactionResult.Success success = mock(TransactionResult.Success.class);
        when(transactionService.deleteTransactionByIdSoft(1L)).thenReturn(CompletableFuture.completedFuture(success));

        ResponseEntity<Void> response = transactionController.deleteTransaction(1L, "soft").get();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteTransaction_Hard_Success() throws Exception {
        TransactionResult.Success success = mock(TransactionResult.Success.class);
        when(transactionService.deleteTransactionByIdHard(1L)).thenReturn(CompletableFuture.completedFuture(success));

        ResponseEntity<Void> response = transactionController.deleteTransaction(1L, "hard").get();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteTransaction_InvalidType() throws Exception {
        ResponseEntity<Void> response = transactionController.deleteTransaction(1L, "unknown").get();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteTransaction_NotFound() throws Exception {
        TransactionResult.NotFoundError notFound = new TransactionResult.NotFoundError("Missing");
        when(transactionService.deleteTransactionByIdSoft(1L)).thenReturn(CompletableFuture.completedFuture(notFound));

        ResponseEntity<Void> response = transactionController.deleteTransaction(1L, "soft").get();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteTransaction_ValidationError() throws Exception {
        TransactionResult.ValidationError validation = new TransactionResult.ValidationError("Invalid");
        when(transactionService.deleteTransactionByIdSoft(1L)).thenReturn(CompletableFuture.completedFuture(validation));

        ResponseEntity<Void> response = transactionController.deleteTransaction(1L, "soft").get();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteTransaction_GenericError() throws Exception {
        TransactionResult.GenericError generic = new TransactionResult.GenericError("Generic");
        when(transactionService.deleteTransactionByIdSoft(1L)).thenReturn(CompletableFuture.completedFuture(generic));

        ResponseEntity<Void> response = transactionController.deleteTransaction(1L, "soft").get();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDeleteTransaction_Exceptionally() throws Exception {
        CompletableFuture<TransactionResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Unexpected"));
        when(transactionService.deleteTransactionByIdSoft(1L)).thenReturn(future);

        ResponseEntity<Void> response = transactionController.deleteTransaction(1L, "soft").get();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetTransactionsBySellerId_Success() throws Exception {
        TransactionFlatView view = mock(TransactionFlatView.class);
        when(transactionService.getTransactionsBySellerId(1L)).thenReturn(CompletableFuture.completedFuture(List.of(view)));

        ResponseEntity<List<TransactionFlatView>> response = transactionController.getTransactionsBySellerId(1L).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void testGetTransactionsBySellerId_Exceptionally() throws Exception {
        CompletableFuture<List<TransactionFlatView>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Unexpected"));
        when(transactionService.getTransactionsBySellerId(1L)).thenReturn(future);

        ResponseEntity<List<TransactionFlatView>> response = transactionController.getTransactionsBySellerId(1L).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
