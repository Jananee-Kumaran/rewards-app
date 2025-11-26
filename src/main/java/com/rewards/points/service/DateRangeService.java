package com.rewards.points.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateRangeService {

    public LocalDate[] resolveRange(LocalDate start, LocalDate end) {
        if (start != null && end != null) return new LocalDate[]{start, end};
        LocalDate calculatedEnd = LocalDate.now();
        LocalDate calculatedStart = calculatedEnd.minusMonths(3);
        return new LocalDate[]{calculatedStart, calculatedEnd};
    }
}
