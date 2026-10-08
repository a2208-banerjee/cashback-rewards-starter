package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Purchase;

import java.util.List;

public interface FindCustomerPurchasesPort {

	List<Purchase> findByCustomerId(String customerId);
}
