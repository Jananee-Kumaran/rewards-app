package com.rewards.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResponse {

    private Long customerId;
    private String customerName;
    private List<MonthlyPointDto> monthlyPoints;
    private Integer totalPoints;
    private Double totalAmount;
    private List<TransactionDto> transactions;
}