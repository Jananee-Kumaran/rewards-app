package com.rewards.app.util;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class RewardPointsCalculator {

	public int calculate(BigDecimal amount) {
		if (amount.compareTo(BigDecimal.valueOf(50)) <= 0)
			return 0;
		else if (amount.compareTo(BigDecimal.valueOf(100)) <= 0)
			return amount.subtract(BigDecimal.valueOf(50)).intValue();
		else
			return amount.subtract(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(2))
					.add(BigDecimal.valueOf(50))
					.intValue();
	}
}
