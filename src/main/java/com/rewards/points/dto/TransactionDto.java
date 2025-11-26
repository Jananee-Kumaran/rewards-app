package com.rewards.points.dto;

public class TransactionDto {
    private Integer id;
    private String date;
    private Double amount;
    private Integer points;

    public TransactionDto(Integer id, String date, Double amount, Integer points) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.points = points;
    }

    public Integer getId() { return id; }
    public String getDate() { return date; }
    public Double getAmount() { return amount; }
    public Integer getPoints() { return points; }
}
