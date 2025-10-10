package com.simplecrm.RequestDTO.Transaction;

import com.simplecrm.Models.Enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionUpdateRequestDTO {
    @NotNull(message = "Seller ID cannot be null for update")
    private Long sellerId;

    @NotNull(message = "Amount cannot be null for update")
    private BigDecimal amount;

    @NotNull(message = "Payment type cannot be null for update")
    private PaymentType paymentType;

    @NotNull(message = "Transaction date cannot be null for update")
    private LocalDateTime transactionDate;

    @NotNull(message = "Version cannot be null for update")
    private Long version;
}
