package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Purchase(String purchaseId, String customerId, String merchantId, BigDecimal amount,
		BigDecimal cashback, Instant postedAt) {
}
