package com.rewards.app.service;

import com.rewards.app.dto.MonthlyPointDto;
import com.rewards.app.dto.RewardResponse;
import com.rewards.app.exception.CustomerNotFoundException;
import com.rewards.app.model.Customer;
import com.rewards.app.model.Transaction;
import com.rewards.app.repository.CustomerRepo;
import com.rewards.app.repository.TransactionRepo;
import com.rewards.app.util.DateRangeUtil;
import com.rewards.app.util.RewardPointsCalculator;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private final CustomerRepo customerRepo;
    private final TransactionRepo transactionRepo;
    private final RewardPointsCalculator calculator;
    private final DateRangeUtil dateRangeUtil;

    public RewardService(CustomerRepo customerRepo,
                         TransactionRepo transactionRepo,
                         RewardPointsCalculator calculator,
                         DateRangeUtil dateRangeUtil) {
        this.customerRepo = customerRepo;
        this.transactionRepo = transactionRepo;
        this.calculator = calculator;
        this.dateRangeUtil = dateRangeUtil;
    }

    public RewardResponse getCustomerRewards(Long customerId, LocalDate start, LocalDate end) {

        validateRange(start, end);

        Customer customer = findCustomer(customerId);

        LocalDate[] range = dateRangeUtil.resolveRange(start, end);

        List<Transaction> transactions =
                transactionRepo.findByCustomerIdAndDateBetween(customerId, range[0], range[1]);

        List<MonthlyPointDto> monthlyPoints = aggregateMonthlyPoints(transactions);

        int totalPoints = monthlyPoints.stream()
                .mapToInt(MonthlyPointDto::getPoints)
                .sum();

        BigDecimal totalAmount = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RewardResponse.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .monthlyPoints(monthlyPoints)
                .totalPoints(totalPoints)
                .totalAmount(totalAmount)
                .build();
    }

    public RewardResponse getCustomerRewards(Long customerId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(3);
        return getCustomerRewards(customerId, start, end);
    }

    private Customer findCustomer(Long customerId) {
        return customerRepo.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found: " + customerId));
    }

    private void validateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (ChronoUnit.MONTHS.between(start, end) > 3) {
            throw new IllegalArgumentException("Date range cannot exceed 3 months");
        }
    }

    private List<MonthlyPointDto> aggregateMonthlyPoints(List<Transaction> transactions) {

        Map<YearMonth, Integer> monthlyMap = new HashMap<>();

        for (Transaction t : transactions) {
            int points = calculator.calculate(t.getAmount()); 
            YearMonth ym = YearMonth.from(t.getDate());
            monthlyMap.merge(ym, points, Integer::sum);
        }

        return monthlyMap.entrySet().stream()
                .map(e -> new MonthlyPointDto(
                        e.getKey().getYear(),
                        e.getKey().getMonth().name(),
                        e.getValue()))
                .collect(Collectors.toList());
    }
}
