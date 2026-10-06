package com.serenitydojo.cashback_rewards.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CashbackCalculator {

	public BigDecimal calculate(BigDecimal purchaseAmount, BigDecimal merchantRate) {
		return purchaseAmount.multiply(merchantRate).setScale(2, RoundingMode.HALF_UP);
	}
}
