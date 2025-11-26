package com.rewards.points.service;

import com.rewards.points.builder.RewardsSummaryBuilder;
import com.rewards.points.dto.TransactionDto;
import com.rewards.points.dto.RewardsSummaryResponse;
import com.rewards.points.model.Customer;
import com.rewards.points.model.Transaction;
import com.rewards.points.repository.CustomerRepo;
import com.rewards.points.repository.TransactionRepo;
import com.rewards.points.exception.CustomerNotFoundException;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
public class RewardsService {

    private final CustomerRepo customerRepo;
    private final TransactionRepo transactionRepo;
    private final RewardPointsCalculator calculator;
    private final DateRangeService dateRangeService;

    public RewardsService(CustomerRepo customerRepo,
                          TransactionRepo transactionRepo,
                          RewardPointsCalculator calculator,
                          DateRangeService dateRangeService) {
        this.customerRepo = customerRepo;
        this.transactionRepo = transactionRepo;
        this.calculator = calculator;
        this.dateRangeService = dateRangeService;
    }

    public RewardsSummaryResponse generateSummary(Integer customerId, LocalDate start, LocalDate end) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        LocalDate[] range = dateRangeService.resolveRange(start, end);
        LocalDate from = range[0];
        LocalDate to = range[1];

        List<Transaction> purchases = transactionRepo.findByCustomerIdAndDateBetween(customerId, from, to);

        Map<Month, Integer> monthly = new HashMap<>();
        List<TransactionDto> details = new ArrayList<>();
        int total = 0;

        for (Transaction p : purchases) {
            int pts = calculator.calculate(p.getAmount());
            total += pts;
            monthly.merge(p.getDate().getMonth(), pts, Integer::sum);
            details.add(new TransactionDto(p.getId(), p.getDate().toString(), p.getAmount(), pts));
        }

        return new RewardsSummaryBuilder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .monthlyPoints(monthly)
                .totalPoints(total)
                .transactions(details)
                .build();
    }

    public List<RewardsSummaryResponse> generateMultiCustomerSummary(Integer months) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(months);
        List<RewardsSummaryResponse> result = new ArrayList<>();
        for (Customer c : customerRepo.findAll()) {
            result.add(generateSummary(c.getId(), start, end));
        }
        return result;
    }
}
