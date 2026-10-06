package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Purchase;

import java.util.Optional;

public interface FindPurchasePort {

	Optional<Purchase> findById(String purchaseId);
}
