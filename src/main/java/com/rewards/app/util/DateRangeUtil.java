package com.rewards.app.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateRangeUtil {

	public LocalDate[] resolveRange(LocalDate startDate, LocalDate endDate) {
		if (startDate != null && endDate != null)
			return new LocalDate[] { startDate, endDate };
		LocalDate calculatedEnd = LocalDate.now();
		LocalDate calculatedStart = calculatedEnd.minusMonths(3);
		return new LocalDate[] { calculatedStart, calculatedEnd };
	}
}
