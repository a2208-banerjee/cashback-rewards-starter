package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;

public record Purchase(String purchaseId, String customerId, String merchantId, BigDecimal amount, BigDecimal cashback) {
}
