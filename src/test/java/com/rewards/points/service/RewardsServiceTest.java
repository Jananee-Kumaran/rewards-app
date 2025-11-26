package com.rewards.points.service;

import com.rewards.points.dto.RewardsSummaryResponse;
import com.rewards.points.exception.CustomerNotFoundException;
import com.rewards.points.model.Customer;
import com.rewards.points.model.Transaction;
import com.rewards.points.repository.CustomerRepo;
import com.rewards.points.repository.TransactionRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RewardsServiceTest {

	private CustomerRepo customerRepo;
	private TransactionRepo transactionRepo;
	private RewardPointsCalculator calculator;
	private DateRangeService dateRangeService;
	private RewardsService rewardsService;

	@BeforeEach
	void setUp() {
		customerRepo = mock(CustomerRepo.class);
		transactionRepo = mock(TransactionRepo.class);
		calculator = new RewardPointsCalculator();
		dateRangeService = new DateRangeService();
		rewardsService = new RewardsService(customerRepo, transactionRepo, calculator, dateRangeService);
	}

	@Test
	void testCustomerNotFound() {
		when(customerRepo.findById(99)).thenReturn(Optional.empty());

		assertThrows(CustomerNotFoundException.class,
				() -> rewardsService.generateSummary(99, LocalDate.now().minusMonths(3), LocalDate.now()));
	}

	@Test
	void testNullDateRangeUsesLast3Months() {
		Customer c = new Customer(1, "John");
		when(customerRepo.findById(1)).thenReturn(Optional.of(c));
		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1), any(), any())).thenReturn(Collections.emptyList());

		RewardsSummaryResponse r = rewardsService.generateSummary(1, null, null);
		assertEquals(0, r.getTotalPoints());
		assertTrue(r.getTransactions().isEmpty());
	}

	@Test
	void testNoTransactionsReturnsZero() {
		Customer c = new Customer(1, "John");
		when(customerRepo.findById(1)).thenReturn(Optional.of(c));
		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1), any(), any())).thenReturn(Collections.emptyList());

		RewardsSummaryResponse r = rewardsService.generateSummary(1, LocalDate.now().minusMonths(3), LocalDate.now());
		assertEquals(0, r.getTotalPoints());
	}

	@Test
	void testAmountLessOrEqual50_NoPoints() {
		Customer c = new Customer(1, "John");
		Transaction t = new Transaction(1, 1, 50.0, LocalDate.now());

		when(customerRepo.findById(1)).thenReturn(Optional.of(c));
		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1), any(), any())).thenReturn(List.of(t));

		RewardsSummaryResponse r = rewardsService.generateSummary(1, LocalDate.now().minusMonths(3), LocalDate.now());
		assertEquals(0, r.getTotalPoints());
	}

	@Test
	void testAmount100() {
		Customer c = new Customer(1, "John");
		Transaction t = new Transaction(1, 1, 100.0, LocalDate.now());

		when(customerRepo.findById(1)).thenReturn(Optional.of(c));
		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1), any(), any())).thenReturn(List.of(t));

		RewardsSummaryResponse r = rewardsService.generateSummary(1, LocalDate.now().minusMonths(3), LocalDate.now());
		assertEquals(50, r.getTotalPoints());
	}

	@Test
	void testAmountAbove100() {
		Customer c = new Customer(1, "John");
		Transaction t = new Transaction(1, 1, 120.0, LocalDate.now());

		when(customerRepo.findById(1)).thenReturn(Optional.of(c));
		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1), any(), any())).thenReturn(List.of(t));

		RewardsSummaryResponse r = rewardsService.generateSummary(1, LocalDate.now().minusMonths(3), LocalDate.now());
		assertEquals(90, r.getTotalPoints());
	}

	@Test
	void testMonthlyAggregation() {
		Customer c = new Customer(1, "John");

		Transaction t1 = new Transaction(1, 1, 120.0, LocalDate.of(2025, 1, 10)); // 90 pts
		Transaction t2 = new Transaction(2, 1, 80.0, LocalDate.of(2025, 2, 5)); // 30 pts

		when(customerRepo.findById(1)).thenReturn(Optional.of(c));
		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1), any(), any())).thenReturn(List.of(t1, t2));

		RewardsSummaryResponse r = rewardsService.generateSummary(1, LocalDate.of(2024, 12, 1),
				LocalDate.of(2025, 3, 1));

		assertEquals(120, r.getTotalPoints());
		assertEquals(90, r.getMonthlyPoints().get(Month.JANUARY));
		assertEquals(30, r.getMonthlyPoints().get(Month.FEBRUARY));
	}

	@Test
	void testMultiCustomerSummary() {

		Customer c1 = new Customer(1, "A");
		Customer c2 = new Customer(2, "B");

		when(customerRepo.findAll()).thenReturn(List.of(c1, c2));

		when(customerRepo.findById(1)).thenReturn(Optional.of(c1));
		when(customerRepo.findById(2)).thenReturn(Optional.of(c2));

		when(transactionRepo.findByCustomerIdAndDateBetween(anyInt(), any(), any()))
				.thenReturn(Collections.emptyList());

		List<RewardsSummaryResponse> list = rewardsService.generateMultiCustomerSummary(3);

		assertEquals(2, list.size());
		assertEquals("A", list.get(0).getCustomerName());
		assertEquals("B", list.get(1).getCustomerName());
	}

}
