package com.simplecrm.Controllers;

import com.simplecrm.AnalyticsDTO.BestPeriodResultDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.Services.Interfaces.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Получить самого продуктивного продавца", description = "Возвращает продавца с наибольшим объёмом продаж за указанный период")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Продавец найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SellerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры даты")
    })
    @GetMapping("/top-seller")
    public CompletableFuture<ResponseEntity<List<SellerResponseDTO>>> getTopSellerByPeriod(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return analyticsService.findTopSellerByPeriod(start, end)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Получить продавцов с суммой меньше указанной", description = "Возвращает список продавцов, сумма продаж которых меньше указанного значения за период")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Продавцы найдены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SellerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры")
    })
    @GetMapping("/low-performers")
    public CompletableFuture<ResponseEntity<List<SellerResponseDTO>>> getSellersWithTotalAmountLessThan(
            @RequestParam BigDecimal amount,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return analyticsService.findSellersWithTotalAmountLessThan(amount, start, end)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Получить самое продуктивное время продавца", description = "Возвращает временной период, когда продавец был наиболее активен")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Период найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BestPeriodResultDTO.class))),
            @ApiResponse(responseCode = "404", description = "Продавец не найден"),
            @ApiResponse(responseCode = "418", description = "Я чайник")
    })
    @GetMapping("/best-period/{sellerId}")
    public CompletableFuture<ResponseEntity<BestPeriodResultDTO>> getBestTransactionPeriodForSeller(
            @PathVariable Long sellerId) {
        return analyticsService.findBestTransactionPeriodForSeller(sellerId)
                .thenApply(result -> {
                    if (result.getTransactionCount() == 0) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new BestPeriodResultDTO());
                    }
                    return ResponseEntity.ok(result);
                })
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new BestPeriodResultDTO()));
    }
}
