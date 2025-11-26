package com.rewards.points.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    private Integer id;

    @Column(name = "customer_id")
    private Integer customerId;

    private Double amount;
    private LocalDate date;

    public Transaction() {}

    public Transaction(Integer id, Integer customerId, Double amount, LocalDate date) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.date = date;
    }

    public Integer getId() { return id; }
    public Integer getCustomerId() { return customerId; }
    public Double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}
