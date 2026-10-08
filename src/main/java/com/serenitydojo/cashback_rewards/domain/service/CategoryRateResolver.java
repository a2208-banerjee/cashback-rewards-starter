package com.serenitydojo.cashback_rewards.domain.service;

import java.math.BigDecimal;
import java.util.Map;

public class CategoryRateResolver {

	private static final BigDecimal GROCERIES_RATE = new BigDecimal("0.02");
	private static final BigDecimal FUEL_RATE = new BigDecimal("0.01");
	private static final BigDecimal DEFAULT_RATE = new BigDecimal("0.005");

	private static final Map<String, BigDecimal> RATE_BY_MCC = Map.of(
			"5411", GROCERIES_RATE,
			"5422", GROCERIES_RATE,
			"5441", GROCERIES_RATE,
			"5451", GROCERIES_RATE,
			"5462", GROCERIES_RATE,
			"5541", FUEL_RATE,
			"5542", FUEL_RATE);

	public BigDecimal rateForMcc(String mcc) {
		if (mcc == null) {
			return DEFAULT_RATE;
		}
		return RATE_BY_MCC.getOrDefault(mcc, DEFAULT_RATE);
	}
}
