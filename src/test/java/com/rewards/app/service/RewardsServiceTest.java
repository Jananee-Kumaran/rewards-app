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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RewardsServiceTest {

	private CustomerRepo customerRepo;
	private TransactionRepo transactionRepo;
	private RewardPointsCalculator calculator;
	private DateRangeUtil dateRangeUtil;
	private RewardService service;

	@BeforeEach
	void setup() {
		customerRepo = mock(CustomerRepo.class);
		transactionRepo = mock(TransactionRepo.class);
		calculator = new RewardPointsCalculator();
		dateRangeUtil = new DateRangeUtil();
		service = new RewardService(customerRepo, transactionRepo, calculator, dateRangeUtil);
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
		assertThat(resp.getTotalAmount()).isEqualByComparingTo("0");
		assertThat(resp.getMonthlyPoints()).isEmpty();
	}

	@Test
	void testPointsBoundaryValues() {
		LocalDate date = LocalDate.of(2024, 1, 1);

		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1L), any(), any()))
				.thenReturn(List.of(new Transaction(1L, 1L, BigDecimal.valueOf(50), date),
						new Transaction(2L, 1L, BigDecimal.valueOf(100), date),
						new Transaction(3L, 1L, BigDecimal.valueOf(101), date)));

		RewardResponse resp = service.getCustomerRewards(1L, date, date.plusDays(1));

		assertThat(resp.getTotalPoints()).isEqualTo(102);
		assertThat(resp.getTotalAmount()).isEqualByComparingTo("251");
	}

	@Test
	void testMonthlyAggregation() {
		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1L), any(), any()))
				.thenReturn(List.of(new Transaction(1L, 1L, BigDecimal.valueOf(120), LocalDate.of(2024, 1, 10)),

						new Transaction(2L, 1L, BigDecimal.valueOf(80), LocalDate.of(2024, 2, 5))));

		RewardResponse resp = service.getCustomerRewards(1L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 28));

		List<MonthlyPointDto> monthly = resp.getMonthlyPoints();

		assertThat(monthly).hasSize(2);
		assertThat(monthly).extracting("month").containsExactlyInAnyOrder("JANUARY", "FEBRUARY");
		assertThat(monthly).extracting("points").containsExactlyInAnyOrder(90, 30);
	}

	@Test
	void testDefaultLastThreeMonths() {
		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1L), any(), any())).thenReturn(List.of());

		RewardResponse resp = service.getCustomerRewards(1L);

		assertThat(resp).isNotNull();
		assertThat(resp.getMonthlyPoints()).isEmpty();
	}

	@Test
	void testDecimalAmount_99Point99() {
		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1L), any(), any()))
				.thenReturn(List.of(new Transaction(1L, 1L, new BigDecimal("99.99"), LocalDate.now())));

		RewardResponse resp = service.getCustomerRewards(1L, LocalDate.now().minusDays(1), LocalDate.now());

		assertThat(resp.getTotalPoints()).isEqualTo(49);
		assertThat(resp.getTotalAmount()).isEqualByComparingTo("99.99");
	}

	@Test
	void testDecimalAmount_150Point75() {
		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1L), any(), any()))
				.thenReturn(List.of(new Transaction(1L, 1L, new BigDecimal("150.75"), LocalDate.now())));

		RewardResponse resp = service.getCustomerRewards(1L, LocalDate.now().minusDays(1), LocalDate.now());

		assertThat(resp.getTotalPoints()).isEqualTo(151);
		assertThat(resp.getTotalAmount()).isEqualByComparingTo("150.75");
	}

	@Test
	void testMultipleDecimalAmounts() {
		when(customerRepo.findById(1L)).thenReturn(Optional.of(new Customer(1L, "John")));

		when(transactionRepo.findByCustomerIdAndDateBetween(eq(1L), any(), any()))
				.thenReturn(List.of(new Transaction(1L, 1L, new BigDecimal("75.25"), LocalDate.now()),
						new Transaction(2L, 1L, new BigDecimal("200.99"), LocalDate.now())));

		RewardResponse resp = service.getCustomerRewards(1L, LocalDate.now().minusDays(1), LocalDate.now());

		assertThat(resp.getTotalPoints()).isEqualTo(25 + 251);
		assertThat(resp.getTotalAmount()).isEqualByComparingTo("276.24");
	}
}
