package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMerchantUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.SaveMerchantPort;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RegisterMerchantService implements RegisterMerchantUseCase {

	private final SaveMerchantPort saveMerchantPort;

	public RegisterMerchantService(SaveMerchantPort saveMerchantPort) {
		this.saveMerchantPort = saveMerchantPort;
	}

	@Override
	public void register(String merchantId, BigDecimal cashbackRate) {
		saveMerchantPort.save(new Merchant(merchantId, cashbackRate));
	}
}
