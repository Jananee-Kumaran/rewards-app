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

	public RewardService(CustomerRepo customerRepo, TransactionRepo transactionRepo, RewardPointsCalculator calculator,
			DateRangeService dateRangeService) {
		this.customerRepo = customerRepo;
		this.transactionRepo = transactionRepo;
		this.calculator = calculator;
		this.dateRangeService = dateRangeService;
	}

	public RewardResponse getCustomerRewards(Long customerId, LocalDate start, LocalDate end) {

		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

		LocalDate[] range = dateRangeService.resolveRange(start, end);
		LocalDate from = range[0];
		LocalDate to = range[1];

		List<Transaction> purchases = transactionRepo.findByCustomerIdAndDateBetween(customerId, from, to);

		Map<YearMonth, Integer> monthAggregation = new HashMap<>();

		List<TransactionDto> details = new ArrayList<>();
		int totalPoints = 0;
		double totalAmount = 0;

		for (Transaction t : purchases) {

			int pts = calculator.calculate(t.getAmount());

			totalPoints += pts;
			totalAmount += t.getAmount();

			YearMonth ym = YearMonth.from(t.getDate());
			monthAggregation.merge(ym, pts, Integer::sum);

			details.add(TransactionDto.builder().id(t.getId()).date(t.getDate().toString()).amount(t.getAmount())
					.points(pts).build());
		}

		List<MonthlyPointDto> monthlyPointList = monthAggregation.entrySet().stream()
				.map(e -> new MonthlyPointDto(e.getKey().getYear(), e.getKey().getMonth().name(), e.getValue()))
				.collect(Collectors.toList());

		return RewardResponse.builder().customerId(customer.getId()).customerName(customer.getName())
				.monthlyPoints(monthlyPointList).totalPoints(totalPoints).totalAmount(totalAmount).transactions(details)
				.build();
	}

	public RewardResponse getCustomerRewards(Long customerId) {
		LocalDate end = LocalDate.now();
		LocalDate start = end.minusMonths(3);
		return getCustomerRewards(customerId, start, end);
	}
}
