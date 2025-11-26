package com.rewards.points.builder;

import com.rewards.points.dto.RewardsSummaryResponse;
import com.rewards.points.dto.TransactionDto;

import java.time.Month;
import java.util.List;
import java.util.Map;

public class RewardsSummaryBuilder {

    private Integer customerId;
    private String customerName;
    private Map<Month, Integer> monthlyPoints;
    private Integer totalPoints;
    private List<TransactionDto> transactions;

    public RewardsSummaryBuilder customerId(Integer id) {
        this.customerId = id; return this;
    }
    public RewardsSummaryBuilder customerName(String name) {
        this.customerName = name; return this;
    }
    public RewardsSummaryBuilder monthlyPoints(Map<Month, Integer> monthlyPoints) {
        this.monthlyPoints = monthlyPoints; return this;
    }
    public RewardsSummaryBuilder totalPoints(Integer total) {
        this.totalPoints = total; return this;
    }
    public RewardsSummaryBuilder transactions(List<TransactionDto> transactions) {
        this.transactions = transactions; return this;
    }

    public RewardsSummaryResponse build() {
        return new RewardsSummaryResponse(customerId, customerName, monthlyPoints, totalPoints, transactions);
    }
}
