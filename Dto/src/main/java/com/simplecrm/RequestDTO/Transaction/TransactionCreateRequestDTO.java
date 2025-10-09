package com.simplecrm.RequestDTO.Transaction;

import com.simplecrm.Models.Enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionCreateRequestDTO {

    @NotNull(message = "Seller ID cannot be null")
    private Long sellerId;

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @NotNull(message = "Payment type cannot be null")
    private PaymentType paymentType;

    @NotNull(message = "Transaction date cannot be null")
    private LocalDateTime transactionDate;
}