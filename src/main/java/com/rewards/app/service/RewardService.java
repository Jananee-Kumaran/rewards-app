package com.rewards.app.service;

import com.rewards.app.dto.MonthlyPointDto;
import com.rewards.app.dto.TransactionDto;
import com.rewards.app.dto.RewardResponse;
import com.rewards.app.model.Customer;
import com.rewards.app.model.Transaction;
import com.rewards.app.repository.CustomerRepo;
import com.rewards.app.repository.TransactionRepo;
import com.rewards.app.util.DateRangeService;
import com.rewards.app.util.RewardPointsCalculator;
import com.rewards.app.exception.CustomerNotFoundException;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private final CustomerRepo customerRepo;
    private final TransactionRepo transactionRepo;
    private final RewardPointsCalculator calculator;
    private final DateRangeService dateRangeService;

    public RewardService(CustomerRepo customerRepo,
                         TransactionRepo transactionRepo,
                         RewardPointsCalculator calculator,
                         DateRangeService dateRangeService) {
        this.customerRepo = customerRepo;
        this.transactionRepo = transactionRepo;
        this.calculator = calculator;
        this.dateRangeService = dateRangeService;
    }

    public RewardResponse getCustomerRewards(Long customerId, LocalDate start, LocalDate end) {

        Customer customer = findCustomer(customerId);
        LocalDate[] range = dateRangeService.resolveRange(start, end);

        List<Transaction> transactions = transactionRepo
                .findByCustomerIdAndDateBetween(customerId, range[0], range[1]);

        List<TransactionDto> transactionDtos = buildTransactionDetails(transactions);
        List<MonthlyPointDto> monthlyPoints = aggregateMonthlyPoints(transactions);

        return buildResponse(customer, monthlyPoints, transactionDtos);
    }

    public RewardResponse getCustomerRewards(Long customerId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(3);
        return getCustomerRewards(customerId, start, end);
    }

    private Customer findCustomer(Long customerId) {
        return customerRepo.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
    }

    private List<TransactionDto> buildTransactionDetails(List<Transaction> transactions) {
        return transactions.stream()
                .map(t -> TransactionDto.builder()
                        .id(t.getId())
                        .date(t.getDate().toString())
                        .amount(t.getAmount())
                        .points(calculator.calculate(t.getAmount()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<MonthlyPointDto> aggregateMonthlyPoints(List<Transaction> transactions) {

        Map<YearMonth, Integer> monthlyAggregation = new HashMap<>();

        for (Transaction t : transactions) {
            int points = calculator.calculate(t.getAmount());
            YearMonth ym = YearMonth.from(t.getDate());
            monthlyAggregation.merge(ym, points, Integer::sum);
        }

        return monthlyAggregation.entrySet().stream()
                .map(e -> new MonthlyPointDto(
                        e.getKey().getYear(),
                        e.getKey().getMonth().name(),
                        e.getValue()))
                .collect(Collectors.toList());
    }

    private RewardResponse buildResponse(Customer customer,
                                         List<MonthlyPointDto> monthlyPoints,
                                         List<TransactionDto> transactions) {

        int totalPoints = transactions.stream().mapToInt(TransactionDto::getPoints).sum();
        double totalAmount = transactions.stream().mapToDouble(TransactionDto::getAmount).sum();

        return RewardResponse.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .monthlyPoints(monthlyPoints)
                .totalPoints(totalPoints)
                .totalAmount(totalAmount)
                .transactions(transactions)
                .build();
    }
}
