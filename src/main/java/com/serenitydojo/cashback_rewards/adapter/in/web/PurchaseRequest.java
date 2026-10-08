package com.serenitydojo.cashback_rewards.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record PurchaseRequest(@NotBlank String customerId, @NotBlank String merchantId, String mcc,
		@NotNull BigDecimal amount, Instant postedAt) {
}
