package com.rewards.app.repository;

import com.rewards.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerIdAndDateBetween(Long customerId, LocalDate start, LocalDate end);
}
