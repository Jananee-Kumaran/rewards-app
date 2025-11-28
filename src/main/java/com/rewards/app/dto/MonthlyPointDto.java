package com.rewards.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyPointDto {
    private int year;
    private String month;
    private int points;
}
