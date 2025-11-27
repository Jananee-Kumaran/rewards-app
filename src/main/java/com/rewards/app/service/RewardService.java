package com.rewards.app.service;

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
import java.time.Month;
import java.util.*;

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

		Map<Month, Integer> monthly = new HashMap<>();
		List<TransactionDto> details = new ArrayList<>();
		int total = 0;
		double totalAmount = 0;

		for (Transaction p : purchases) {
			int pts = calculator.calculate(p.getAmount());
			total += pts;
			totalAmount += p.getAmount();
			monthly.merge(p.getDate().getMonth(), pts, Integer::sum);

			details.add(TransactionDto.builder().id(p.getId()).date(p.getDate().toString()).amount(p.getAmount())
					.points(pts).build());
		}

		return RewardResponse.builder().customerId(customer.getId()).customerName(customer.getName())
				.monthlyPoints(monthly).totalPoints(total).totalAmount(totalAmount).transactions(details).build();
	}

	public RewardResponse getCustomerRewards(Long customerId) {
		LocalDate end = LocalDate.now();
		LocalDate start = end.minusMonths(3);

		return getCustomerRewards(customerId, start, end);
	}
}
