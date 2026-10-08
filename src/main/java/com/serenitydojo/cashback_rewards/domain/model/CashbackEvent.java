package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record CashbackEvent(Instant postedAt, String merchantId, BigDecimal cashback) {
}
