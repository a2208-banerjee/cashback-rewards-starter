package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;

public interface PurchaseUseCase {

	BigDecimal purchase(String customerId, String merchantId, BigDecimal amount);
}
