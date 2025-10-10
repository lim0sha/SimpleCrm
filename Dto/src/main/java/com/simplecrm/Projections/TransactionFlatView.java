package com.simplecrm.Projections;

import com.simplecrm.Models.Enums.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionFlatView {
    Long getId();
    BigDecimal getAmount();
    PaymentType getPaymentType();
    LocalDateTime getTransactionDate();
    Long getVersion();

    SellerView getSeller();

    interface SellerView {
        Long getId();
        String getName();
        String getContactInfo();
        LocalDateTime getRegistrationDate();
        Long getVersion();
    }
}
