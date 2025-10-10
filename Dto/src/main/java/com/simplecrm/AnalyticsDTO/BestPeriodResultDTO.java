package com.simplecrm.AnalyticsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BestPeriodResultDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int transactionCount;
}
