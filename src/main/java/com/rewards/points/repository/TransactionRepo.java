package com.rewards.points.repository;

import com.rewards.points.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByCustomerIdAndDateBetween(Integer customerId, LocalDate start, LocalDate end);
    List<Transaction> findByCustomerId(Integer customerId);
}
