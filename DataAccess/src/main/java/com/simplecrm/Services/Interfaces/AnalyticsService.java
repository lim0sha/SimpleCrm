package com.simplecrm.Services.Interfaces;

import com.simplecrm.AnalyticsDTO.BestPeriodResultDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsService {
    CompletableFuture<List<SellerResponseDTO>> findTopSellerByPeriod(LocalDateTime start, LocalDateTime end);

    CompletableFuture<List<SellerResponseDTO>> findSellersWithTotalAmountLessThan(BigDecimal amount, LocalDateTime start, LocalDateTime end);

    CompletableFuture<BestPeriodResultDTO> findBestTransactionPeriodForSeller(Long sellerId);
}
