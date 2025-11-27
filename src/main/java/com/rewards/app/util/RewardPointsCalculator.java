package com.rewards.app.util;

import org.springframework.stereotype.Component;

@Component
public class RewardPointsCalculator {

    public int calculate(double amount) {
        if (amount <= 50) return 0;
        else if (amount <= 100) return (int)(amount - 50);
        else return (int)((amount - 100) * 2 + 50);
    }
}
