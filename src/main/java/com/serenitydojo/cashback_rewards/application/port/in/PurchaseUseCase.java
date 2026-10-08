package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;

public interface PurchaseUseCase {

	BigDecimal purchase(String customerId, String merchantId, String mcc, BigDecimal amount, Instant postedAt);
}
