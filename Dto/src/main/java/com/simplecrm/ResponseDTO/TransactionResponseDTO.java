package com.simplecrm.ResponseDTO;

import com.simplecrm.Models.Enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponseDTO {
    private Long id;
    private SellerResponseDTO seller;
    private BigDecimal amount;
    private PaymentType paymentType;
    private LocalDateTime transactionDate;
    private Long version;
}