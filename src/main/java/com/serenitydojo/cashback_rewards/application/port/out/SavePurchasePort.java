package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Purchase;

public interface SavePurchasePort {

	void save(Purchase purchase);
}
