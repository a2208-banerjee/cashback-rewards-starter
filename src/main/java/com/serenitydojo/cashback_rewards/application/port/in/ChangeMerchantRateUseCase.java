package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;

public interface ChangeMerchantRateUseCase {

	void changeRate(String merchantId, BigDecimal newCashbackRate);
}
