package com.simplecrm.api;

import com.simplecrm.AnalyticsDTO.BestPeriodResultDTO;
import com.simplecrm.Controllers.AnalyticsController;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.Services.Interfaces.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AnalyticsController.class)
@Import({AnalyticsControllerAPITest.TestConfig.class, AnalyticsController.class})
class AnalyticsControllerAPITest {

    @Configuration
    static class TestConfig {
        @Bean
        public AnalyticsService analyticsService() {
            return Mockito.mock(AnalyticsService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    void getTopSellerByPeriod_success() throws Exception {
        SellerResponseDTO seller = new SellerResponseDTO();
        seller.setId(1L);
        when(analyticsService.findTopSellerByPeriod(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(List.of(seller)));

        MvcResult mvcResult = mockMvc.perform(get("/api/analytics/top-seller")
                        .param("start", "2025-01-01T00:00:00")
                        .param("end", "2025-01-31T23:59:59"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getTopSellerByPeriod_invalidDates() throws Exception {
        when(analyticsService.findTopSellerByPeriod(any(), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Invalid dates")));

        MvcResult mvcResult = mockMvc.perform(get("/api/analytics/top-seller")
                        .param("start", "2025-01-31T00:00:00")
                        .param("end", "2025-01-01T00:00:00"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSellersWithTotalAmountLessThan_success() throws Exception {
        SellerResponseDTO seller = new SellerResponseDTO();
        seller.setId(2L);
        when(analyticsService.findSellersWithTotalAmountLessThan(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(List.of(seller)));

        MvcResult mvcResult = mockMvc.perform(get("/api/analytics/low-performers")
                        .param("amount", "1000")
                        .param("start", "2025-01-01T00:00:00")
                        .param("end", "2025-01-31T23:59:59"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void getSellersWithTotalAmountLessThan_invalidParams() throws Exception {
        when(analyticsService.findSellersWithTotalAmountLessThan(any(), any(), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Invalid params")));

        MvcResult mvcResult = mockMvc.perform(get("/api/analytics/low-performers")
                        .param("amount", "-10")
                        .param("start", "2025-01-31T00:00:00")
                        .param("end", "2025-01-01T00:00:00"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBestTransactionPeriodForSeller_success() throws Exception {
        BestPeriodResultDTO result = new BestPeriodResultDTO(LocalDateTime.of(2025,1,1,0,0),
                LocalDateTime.of(2025,1,2,0,0), 5);
        when(analyticsService.findBestTransactionPeriodForSeller(1L))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(get("/api/analytics/best-period/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionCount").value(5));
    }

    @Test
    void getBestTransactionPeriodForSeller_notFound() throws Exception {
        BestPeriodResultDTO result = new BestPeriodResultDTO();
        when(analyticsService.findBestTransactionPeriodForSeller(1L))
                .thenReturn(CompletableFuture.completedFuture(result));

        MvcResult mvcResult = mockMvc.perform(get("/api/analytics/best-period/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.transactionCount").value(0));
    }

    @Test
    void getBestTransactionPeriodForSeller_exception() throws Exception {
        when(analyticsService.findBestTransactionPeriodForSeller(1L))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(get("/api/analytics/best-period/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.transactionCount").value(0));
    }
}
