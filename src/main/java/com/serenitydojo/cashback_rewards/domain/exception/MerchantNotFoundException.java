package com.serenitydojo.cashback_rewards.domain.exception;

public class MerchantNotFoundException extends RuntimeException {

	public MerchantNotFoundException(String merchantId) {
		super("Merchant not found: " + merchantId);
	}
}
