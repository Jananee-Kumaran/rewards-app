package com.rewards.points.dto;

import java.time.Month;
import java.util.List;
import java.util.Map;

public class RewardsSummaryResponse {
    private Integer customerId;
    private String customerName;
    private Map<Month, Integer> monthlyPoints;
    private Integer totalPoints;
    private List<TransactionDto> transactions;

    public RewardsSummaryResponse(Integer customerId, String customerName,
                                  Map<Month, Integer> monthlyPoints,
                                  Integer totalPoints,
                                  List<TransactionDto> transactions) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyPoints = monthlyPoints;
        this.totalPoints = totalPoints;
        this.transactions = transactions;
    }

    public Integer getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public Map<Month, Integer> getMonthlyPoints() { return monthlyPoints; }
    public Integer getTotalPoints() { return totalPoints; }
    public List<TransactionDto> getTransactions() { return transactions; }
}
