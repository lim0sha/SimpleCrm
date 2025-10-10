package com.simplecrm.Services;

import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.Projections.TransactionFlatView;
import com.simplecrm.Repositories.SellerRepository;
import com.simplecrm.Repositories.TransactionRepository;
import com.simplecrm.RequestDTO.Transaction.TransactionCreateRequestDTO;
import com.simplecrm.RequestDTO.Transaction.TransactionUpdateRequestDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import com.simplecrm.ResultTypes.TransactionResult;
import com.simplecrm.Services.Interfaces.TransactionService;
import com.simplecrm.Utils.Mapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;
    private final Mapper mapper;

    @Override
    @Async
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<TransactionResult> createTransaction(@Valid TransactionCreateRequestDTO requestDto) {
        try {
            return sellerRepository.findNotDeletedById(requestDto.getSellerId())
                    .map(seller -> {
                        Transaction transaction = new Transaction();
                        transaction.setSeller(seller);
                        transaction.setAmount(requestDto.getAmount());
                        transaction.setPaymentType(requestDto.getPaymentType());
                        transaction.setTransactionDate(requestDto.getTransactionDate() != null
                                ? requestDto.getTransactionDate()
                                : LocalDateTime.now());

                        Transaction savedEntity = transactionRepository.save(transaction);
                        TransactionResponseDTO responseDto = mapper.mapEntityToTransactionResponseDto(savedEntity);

                        return CompletableFuture.completedFuture((TransactionResult) new TransactionResult.Success(responseDto));
                    })
                    .orElse(CompletableFuture.completedFuture(
                            new TransactionResult.SellerNotFoundError("Seller not found with id: " + requestDto.getSellerId())
                    ));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    new TransactionResult.GenericError("Error creating transaction: " + e.getMessage())
            );
        }
    }


    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<TransactionResult> getTransactionById(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.completedFuture(new TransactionResult.ValidationError("Transaction ID must be positive"));
        }

        return CompletableFuture.completedFuture(
                transactionRepository.findNotDeletedById(id)
                        .map(transaction -> (TransactionResult) new TransactionResult.Success(mapper.mapEntityToTransactionResponseDto(transaction)))
                        .orElse(new TransactionResult.NotFoundError("Transaction not found with id: " + id))
        );
    }

    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<List<TransactionResponseDTO>> getAllTransactions() {
        try {
            List<TransactionResponseDTO> transactions = transactionRepository.findAllNotDeleted().stream()
                    .map(mapper::mapEntityToTransactionResponseDto)
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(transactions);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(List.of());
        }
    }

    @Override
    @Async
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<TransactionResult> updateTransactionById(Long id, @Valid TransactionUpdateRequestDTO requestDto) {
        if (id == null || id <= 0) {
            return CompletableFuture.completedFuture(new TransactionResult.ValidationError("Transaction ID must be positive"));
        }

        return CompletableFuture.completedFuture(
                transactionRepository.findNotDeletedById(id)
                        .map(existingTransaction -> {
                            try {
                                if (!existingTransaction.getVersion().equals(requestDto.getVersion())) {
                                    return new TransactionResult.ValidationError(
                                            "Data is stale, please refresh and try again. Expected version: "
                                                    + requestDto.getVersion() + ", but found: " + existingTransaction.getVersion()
                                    );
                                }

                                if (requestDto.getSellerId() != null) {
                                    return sellerRepository.findNotDeletedById(requestDto.getSellerId())
                                            .map(newSeller -> {
                                                existingTransaction.setSeller(newSeller);
                                                existingTransaction.setAmount(requestDto.getAmount());
                                                existingTransaction.setPaymentType(requestDto.getPaymentType());
                                                existingTransaction.setTransactionDate(requestDto.getTransactionDate());
                                                Transaction updatedEntity = transactionRepository.save(existingTransaction);
                                                TransactionResponseDTO responseDto = mapper.mapEntityToTransactionResponseDto(updatedEntity);
                                                return (TransactionResult) new TransactionResult.Success(responseDto);
                                            })
                                            .orElse(new TransactionResult.SellerNotFoundError("Seller not found with id: " + requestDto.getSellerId()));
                                } else {
                                    existingTransaction.setAmount(requestDto.getAmount());
                                    existingTransaction.setPaymentType(requestDto.getPaymentType());
                                    existingTransaction.setTransactionDate(requestDto.getTransactionDate());
                                    Transaction updatedEntity = transactionRepository.save(existingTransaction);
                                    TransactionResponseDTO responseDto = mapper.mapEntityToTransactionResponseDto(updatedEntity);
                                    return new TransactionResult.Success(responseDto);
                                }
                            } catch (ObjectOptimisticLockingFailureException e) {
                                return new TransactionResult.GenericError("Concurrent update error. Please try again.");
                            } catch (Exception e) {
                                return new TransactionResult.GenericError("Error updating transaction: " + e.getMessage());
                            }
                        })
                        .orElse(new TransactionResult.NotFoundError("Transaction not found with id: " + id))
        );
    }

    @Override
    @Async
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<TransactionResult> deleteTransactionByIdSoft(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.completedFuture(new TransactionResult.ValidationError("Transaction ID must be positive"));
        }

        return CompletableFuture.completedFuture(
                transactionRepository.findNotDeletedById(id)
                        .map(transaction -> {
                            try {
                                transaction.setDeleted(true);
                                transactionRepository.save(transaction);
                                return new TransactionResult.Success(mapper.mapEntityToTransactionResponseDto(transaction));
                            } catch (Exception e) {
                                return new TransactionResult.GenericError("Error deleting transaction: " + e.getMessage());
                            }
                        })
                        .orElse(new TransactionResult.NotFoundError("Transaction not found with id: " + id))
        );
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<TransactionResult> deleteTransactionByIdHard(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (id == null || id <= 0) {
                    return new TransactionResult.ValidationError("Invalid transaction ID: " + id);
                }

                if (!transactionRepository.existsById(id)) {
                    return new TransactionResult.NotFoundError("Transaction not found with id: " + id);
                }

                transactionRepository.deleteById(id);
                return new TransactionResult.Success(null);

            } catch (Exception e) {
                return new TransactionResult.GenericError("Error deleting transaction: " + e.getMessage());
            }
        });
    }

    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<List<TransactionFlatView>> getTransactionsBySellerId(Long sellerId) {
        try {
            if (sellerId == null || sellerId <= 0) {
                return CompletableFuture.completedFuture(List.of());
            }

            List<TransactionFlatView> transactions = transactionRepository.findFlatBySellerId(sellerId);
            return CompletableFuture.completedFuture(transactions);

        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<List<TransactionResponseDTO>> getTransactionsBySellerIdAndDateRange(Long sellerId, LocalDateTime start, LocalDateTime end) {
        if (sellerId == null || sellerId <= 0 || start == null || end == null || start.isAfter(end)) {
            return CompletableFuture.completedFuture(List.of());
        }

        try {
            List<TransactionResponseDTO> transactions = transactionRepository.findBySellerIdAndDateRange(sellerId, start, end).stream()
                    .map(mapper::mapEntityToTransactionResponseDto)
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(transactions);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(List.of());
        }
    }

    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<List<TransactionResponseDTO>> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || start.isAfter(end)) {
            return CompletableFuture.completedFuture(List.of());
        }

        try {
            List<TransactionResponseDTO> transactions = transactionRepository.findByDateRange(start, end).stream()
                    .map(mapper::mapEntityToTransactionResponseDto)
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(transactions);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(List.of());
        }
    }
}
