package com.rewards.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    private Double amount;
    private LocalDate date;
}
