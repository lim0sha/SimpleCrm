package com.simplecrm.Services;

import com.simplecrm.AnalyticsDTO.BestPeriodResultDTO;
import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.Repositories.SellerRepository;
import com.simplecrm.Repositories.TransactionRepository;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.Services.Interfaces.AnalyticsService;
import com.simplecrm.Utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SellerRepository sellerRepository;
    private final TransactionRepository transactionRepository;
    private final Mapper mapper;

    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<List<SellerResponseDTO>> findTopSellerByPeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || start.isAfter(end)) {
            return CompletableFuture.completedFuture(List.of());
        }
        try {
            List<Seller> topSellers = sellerRepository.findTopSellerByPeriod(start, end);
            List<SellerResponseDTO> result = topSellers.stream()
                    .limit(1)
                    .map(mapper::mapEntityToSellerResponseDto)
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(List.of());
        }
    }

    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<List<SellerResponseDTO>> findSellersWithTotalAmountLessThan(BigDecimal amount, LocalDateTime start, LocalDateTime end) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0 || start == null || end == null || start.isAfter(end)) {
            return CompletableFuture.completedFuture(List.of());
        }
        try {
            List<Seller> sellers = sellerRepository.findSellersWithAmountLessThan(amount, start, end);
            List<SellerResponseDTO> result = sellers.stream()
                    .map(mapper::mapEntityToSellerResponseDto)
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(List.of());
        }
    }

    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<BestPeriodResultDTO> findBestTransactionPeriodForSeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return CompletableFuture.completedFuture(new BestPeriodResultDTO());
        }

        try {
            List<Transaction> transactions = transactionRepository.findBySellerIdAndNotDeleted(sellerId)
                    .stream()
                    .sorted(Comparator.comparing(Transaction::getTransactionDate))
                    .toList();

            if (transactions.isEmpty()) {
                return CompletableFuture.completedFuture(new BestPeriodResultDTO());
            }

            int maxCount = 0;
            LocalDateTime bestStart = null;
            LocalDateTime bestEnd = null;

            for (int i = 0; i < transactions.size(); i++) {
                for (int j = i; j < transactions.size(); j++) {
                    int count = j - i + 1;
                    if (count > maxCount) {
                        maxCount = count;
                        bestStart = transactions.get(i).getTransactionDate();
                        bestEnd = transactions.get(j).getTransactionDate();
                    }
                }
            }

            BestPeriodResultDTO result = new BestPeriodResultDTO(bestStart, bestEnd, maxCount);
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("Error finding best transaction period", e));
        }
    }
}
