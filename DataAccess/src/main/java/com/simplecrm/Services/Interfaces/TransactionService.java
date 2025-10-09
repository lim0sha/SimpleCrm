package com.simplecrm.Services.Interfaces;

import com.simplecrm.Projections.TransactionFlatView;
import com.simplecrm.RequestDTO.Transaction.TransactionCreateRequestDTO;
import com.simplecrm.RequestDTO.Transaction.TransactionUpdateRequestDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import com.simplecrm.ResultTypes.TransactionResult;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {

    CompletableFuture<TransactionResult> createTransaction(@Valid TransactionCreateRequestDTO requestDto);

    CompletableFuture<TransactionResult> getTransactionById(Long id);

    CompletableFuture<TransactionResult> updateTransactionById(Long id, @Valid TransactionUpdateRequestDTO requestDto);

    CompletableFuture<TransactionResult> deleteTransactionByIdSoft(Long id);

    CompletableFuture<TransactionResult> deleteTransactionByIdHard(Long id);

    CompletableFuture<List<TransactionResponseDTO>> getAllTransactions();

    CompletableFuture<List<TransactionFlatView>> getTransactionsBySellerId(Long sellerId);

    CompletableFuture<List<TransactionResponseDTO>> getTransactionsBySellerIdAndDateRange(Long sellerId, LocalDateTime start, LocalDateTime end);

    CompletableFuture<List<TransactionResponseDTO>> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end);
}
