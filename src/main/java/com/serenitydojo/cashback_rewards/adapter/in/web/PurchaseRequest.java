package com.serenitydojo.cashback_rewards.adapter.in.web;

import java.math.BigDecimal;

public record PurchaseRequest(String customerId, String merchantId, BigDecimal amount) {
}
