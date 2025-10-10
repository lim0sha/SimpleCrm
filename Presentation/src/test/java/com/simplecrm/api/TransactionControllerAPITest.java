package com.simplecrm.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecrm.Controllers.TransactionController;
import com.simplecrm.Projections.TransactionFlatView;
import com.simplecrm.RequestDTO.Transaction.TransactionCreateRequestDTO;
import com.simplecrm.RequestDTO.Transaction.TransactionUpdateRequestDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import com.simplecrm.ResultTypes.TransactionResult;
import com.simplecrm.Models.Enums.PaymentType;
import com.simplecrm.Services.Interfaces.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class)
@Import({TransactionControllerAPITest.TestConfig.class, TransactionController.class})
class TransactionControllerAPITest {

    @Configuration
    static class TestConfig {
        @Bean
        public TransactionService transactionService() {
            return org.mockito.Mockito.mock(TransactionService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void getAllTransactions_success() throws Exception {
        TransactionResponseDTO t1 = new TransactionResponseDTO();
        t1.setId(1L);
        TransactionResponseDTO t2 = new TransactionResponseDTO();
        t2.setId(2L);

        when(transactionService.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(List.of(t1, t2)));

        MvcResult mvcResult = mockMvc.perform(get("/api/transactions"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllTransactions_error() throws Exception {
        when(transactionService.getAllTransactions())
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(get("/api/transactions"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getTransactionById_success() throws Exception {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(1L);
        TransactionResult.Success result = new TransactionResult.Success(dto);
        when(transactionService.getTransactionById(1L))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(get("/api/transactions/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction.id").value(1L));
    }

    @Test
    void getTransactionById_notFound() throws Exception {
        TransactionResult.NotFoundError result = new TransactionResult.NotFoundError("Not found");
        when(transactionService.getTransactionById(1L))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(get("/api/transactions/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void getTransactionById_validationError() throws Exception {
        TransactionResult.ValidationError result = new TransactionResult.ValidationError("Invalid ID");
        when(transactionService.getTransactionById(-1L))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(get("/api/transactions/-1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid ID"));
    }

    @Test
    void getTransactionById_genericError() throws Exception {
        TransactionResult.GenericError result = new TransactionResult.GenericError("Server error");
        when(transactionService.getTransactionById(1L))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(get("/api/transactions/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Server error"));
    }

    @Test
    void getTransactionById_exception() throws Exception {
        when(transactionService.getTransactionById(1L))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(get("/api/transactions/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error: java.lang.RuntimeException: DB error"));
    }

    @Test
    void createTransaction_success() throws Exception {
        TransactionCreateRequestDTO req = new TransactionCreateRequestDTO();
        req.setSellerId(1L);
        req.setAmount(BigDecimal.TEN);
        req.setPaymentType(PaymentType.CASH);
        req.setTransactionDate(LocalDateTime.now());

        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(1L);
        TransactionResult.Success result = new TransactionResult.Success(dto);

        when(transactionService.createTransaction(any()))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction.id").value(1L));
    }

    @Test
    void createTransaction_validationError() throws Exception {
        TransactionCreateRequestDTO req = new TransactionCreateRequestDTO();
        TransactionResult.ValidationError result = new TransactionResult.ValidationError("Invalid data");
        when(transactionService.createTransaction(any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> result));

        MvcResult mvcResult = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"));
    }

    @Test
    void createTransaction_notFoundSeller() throws Exception {
        TransactionCreateRequestDTO req = new TransactionCreateRequestDTO();
        req.setSellerId(99L);
        req.setAmount(BigDecimal.TEN);
        req.setPaymentType(PaymentType.CARD);
        req.setTransactionDate(LocalDateTime.now());

        TransactionResult.SellerNotFoundError result = new TransactionResult.SellerNotFoundError("Seller not found");
        when(transactionService.createTransaction(any()))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seller not found"));
    }

    @Test
    void createTransaction_exception() throws Exception {
        TransactionCreateRequestDTO req = new TransactionCreateRequestDTO();
        req.setSellerId(1L);
        req.setAmount(BigDecimal.TEN);
        req.setPaymentType(PaymentType.CARD);
        req.setTransactionDate(LocalDateTime.now());

        when(transactionService.createTransaction(any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error: java.lang.RuntimeException: DB error"));
    }

    @Test
    void updateTransaction_success() throws Exception {
        TransactionUpdateRequestDTO req = new TransactionUpdateRequestDTO();
        req.setSellerId(1L);
        req.setAmount(BigDecimal.TEN);
        req.setPaymentType(PaymentType.CARD);
        req.setTransactionDate(LocalDateTime.now());
        req.setVersion(1L);

        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(1L);
        TransactionResult.Success result = new TransactionResult.Success(dto);

        when(transactionService.updateTransactionById(anyLong(), any()))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction.id").value(1L));
    }

    @Test
    void updateTransaction_notFound() throws Exception {
        TransactionUpdateRequestDTO req = new TransactionUpdateRequestDTO();
        req.setSellerId(1L);
        req.setAmount(BigDecimal.TEN);
        req.setPaymentType(PaymentType.CARD);
        req.setTransactionDate(LocalDateTime.now());
        req.setVersion(1L);

        TransactionResult.NotFoundError result = new TransactionResult.NotFoundError("Not found");
        when(transactionService.updateTransactionById(anyLong(), any()))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void updateTransaction_exception() throws Exception {
        TransactionUpdateRequestDTO req = new TransactionUpdateRequestDTO();
        req.setSellerId(1L);
        req.setAmount(BigDecimal.TEN);
        req.setPaymentType(PaymentType.CARD);
        req.setTransactionDate(LocalDateTime.now());
        req.setVersion(1L);

        when(transactionService.updateTransactionById(anyLong(), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error: java.lang.RuntimeException: DB error"));
    }

    @Test
    void deleteTransaction_soft_success() throws Exception {
        TransactionResult.Success result = new TransactionResult.Success(null);
        when(transactionService.deleteTransactionByIdSoft(1L))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(delete("/api/transactions/1")
                        .param("deleteType", "soft"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTransaction_invalidType() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/transactions/1")
                        .param("deleteType", "invalid"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTransaction_exception() throws Exception {
        when(transactionService.deleteTransactionByIdSoft(1L))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(delete("/api/transactions/1")
                        .param("deleteType", "soft"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getTransactionsBySellerId_success() throws Exception {
        TransactionFlatView flatView = new TransactionFlatView() {
            public Long getId() { return 1L; }
            public BigDecimal getAmount() { return BigDecimal.TEN; }
            public PaymentType getPaymentType() { return PaymentType.CARD; }
            public LocalDateTime getTransactionDate() { return LocalDateTime.now(); }
            public Long getVersion() { return 1L; }
            public SellerView getSeller() { return null; }
        };

        when(transactionService.getTransactionsBySellerId(1L))
                .thenReturn(CompletableFuture.completedFuture(List.of(flatView)));

        MvcResult mvcResult = mockMvc.perform(get("/api/transactions/seller/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getTransactionsBySellerId_error() throws Exception {
        when(transactionService.getTransactionsBySellerId(1L))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(get("/api/transactions/seller/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError());
    }
}
