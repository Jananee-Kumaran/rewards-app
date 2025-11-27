package com.rewards.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResponse {

    private Long customerId;
    private String customerName;
    private Map<Month, Integer> monthlyPoints;
    private Integer totalPoints;
    private Double totalAmount;
    private List<TransactionDto> transactions;
}
