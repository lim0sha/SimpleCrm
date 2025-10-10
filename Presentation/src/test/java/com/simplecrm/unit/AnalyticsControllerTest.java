package com.simplecrm.unit;

import com.simplecrm.AnalyticsDTO.BestPeriodResultDTO;
import com.simplecrm.Controllers.AnalyticsController;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.Services.Interfaces.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTopSellerByPeriod_Success() throws Exception {
        SellerResponseDTO dto = new SellerResponseDTO();
        when(analyticsService.findTopSellerByPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(dto)));

        ResponseEntity<List<SellerResponseDTO>> response = analyticsController
                .getTopSellerByPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(1)).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void testGetTopSellerByPeriod_Exceptionally() throws Exception {
        CompletableFuture<List<SellerResponseDTO>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("error"));
        when(analyticsService.findTopSellerByPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(future);

        ResponseEntity<List<SellerResponseDTO>> response = analyticsController
                .getTopSellerByPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(1)).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetSellersWithTotalAmountLessThan_Success() throws Exception {
        SellerResponseDTO dto = new SellerResponseDTO();
        when(analyticsService.findSellersWithTotalAmountLessThan(any(BigDecimal.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(dto)));

        ResponseEntity<List<SellerResponseDTO>> response = analyticsController
                .getSellersWithTotalAmountLessThan(BigDecimal.valueOf(1000),
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1)).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void testGetSellersWithTotalAmountLessThan_Exceptionally() throws Exception {
        CompletableFuture<List<SellerResponseDTO>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("error"));
        when(analyticsService.findSellersWithTotalAmountLessThan(any(BigDecimal.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(future);

        ResponseEntity<List<SellerResponseDTO>> response = analyticsController
                .getSellersWithTotalAmountLessThan(BigDecimal.valueOf(1000),
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1)).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetBestTransactionPeriodForSeller_Found() throws Exception {
        BestPeriodResultDTO dto = new BestPeriodResultDTO(LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), 5);
        when(analyticsService.findBestTransactionPeriodForSeller(1L))
                .thenReturn(CompletableFuture.completedFuture(dto));

        ResponseEntity<BestPeriodResultDTO> response = analyticsController
                .getBestTransactionPeriodForSeller(1L).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, Objects.requireNonNull(response.getBody()).getTransactionCount());
    }

    @Test
    void testGetBestTransactionPeriodForSeller_NotFound() throws Exception {
        BestPeriodResultDTO dto = new BestPeriodResultDTO();
        when(analyticsService.findBestTransactionPeriodForSeller(1L))
                .thenReturn(CompletableFuture.completedFuture(dto));

        ResponseEntity<BestPeriodResultDTO> response = analyticsController
                .getBestTransactionPeriodForSeller(1L).get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(0, Objects.requireNonNull(response.getBody()).getTransactionCount());
    }

    @Test
    void testGetBestTransactionPeriodForSeller_Exceptionally() throws Exception {
        CompletableFuture<BestPeriodResultDTO> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("error"));
        when(analyticsService.findBestTransactionPeriodForSeller(1L)).thenReturn(future);

        ResponseEntity<BestPeriodResultDTO> response = analyticsController
                .getBestTransactionPeriodForSeller(1L).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(0, Objects.requireNonNull(response.getBody()).getTransactionCount());
    }
}
