package com.rewards.app.service;

import com.rewards.app.dto.RewardResponse;
import com.rewards.app.exception.CustomerNotFoundException;
import com.rewards.app.model.Customer;
import com.rewards.app.model.Transaction;
import com.rewards.app.repository.CustomerRepo;
import com.rewards.app.repository.TransactionRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

class RewardsServiceTest {

	private CustomerRepo customerRepo;
	private TransactionRepo transactionRepo;
	private RewardPointsCalculator calculator;
	private DateRangeService dateRangeService;
	private RewardService service;

	@BeforeEach
	void setup() {
		customerRepo = mock(CustomerRepo.class);
		transactionRepo = mock(TransactionRepo.class);
		calculator = new RewardPointsCalculator();
		dateRangeService = new DateRangeService();
		service = new RewardService(customerRepo, transactionRepo, calculator, dateRangeService);
	}

	@Test
	void testCustomerNotFound() {
		when(customerRepo.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getCustomerRewards(1L, LocalDate.now().minusMonths(1), LocalDate.now()))
				.isInstanceOf(CustomerNotFoundException.class);
	}

	@Test
	void testNoTransactions() {
		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(anyLong(), any(), any())).thenReturn(List.of());

		RewardResponse resp = service.getCustomerRewards(1L, LocalDate.now().minusMonths(1), LocalDate.now());

		assertThat(resp.getTotalPoints()).isEqualTo(0);
		assertThat(resp.getTotalAmount()).isEqualTo(0);
		assertThat(resp.getTransactions()).isEmpty();
	}

	@Test
	void testPointsBoundaryValues() {

		LocalDate date = LocalDate.of(2024, 1, 1);

		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1L), any(), any()))
				.thenReturn(List.of(new Transaction(1L, 1L, 50.0, date), // 0 pts
						new Transaction(2L, 1L, 100.0, date), // 50 pts
						new Transaction(3L, 1L, 101.0, date) // 52 pts
				));

		RewardResponse resp = service.getCustomerRewards(1L, date, date.plusDays(1));

		assertThat(resp.getTotalPoints()).isEqualTo(0 + 50 + 52);
		assertThat(resp.getTotalAmount()).isEqualTo(50.0 + 100.0 + 101.0);
	}

	@Test
	void testMonthlyAggregation() {

		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1L), any(), any()))
				.thenReturn(List.of(new Transaction(1L, 1L, 120.0, LocalDate.of(2024, 1, 10)), // 90 pts
						new Transaction(2L, 1L, 80.0, LocalDate.of(2024, 2, 5)) // 30 pts
				));

		RewardResponse resp = service.getCustomerRewards(1L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 20));

		assertThat(resp.getMonthlyPoints().get(java.time.Month.JANUARY)).isEqualTo(90);
		assertThat(resp.getMonthlyPoints().get(java.time.Month.FEBRUARY)).isEqualTo(30);
	}

	@Test
	void testDefaultLastThreeMonths() {

		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1L), any(), any())).thenReturn(List.of());

		RewardResponse resp = service.getCustomerRewards(1L);

		assertThat(resp).isNotNull();
	}
}
