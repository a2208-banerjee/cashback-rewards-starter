package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.PurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.exception.MerchantNotFoundException;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PurchaseService implements PurchaseUseCase {

	private final MerchantRepository merchantRepository;
	private final CashbackCalculator cashbackCalculator;

	public PurchaseService(MerchantRepository merchantRepository, CashbackCalculator cashbackCalculator) {
		this.merchantRepository = merchantRepository;
		this.cashbackCalculator = cashbackCalculator;
	}

	@Override
	public BigDecimal purchase(String customerId, String merchantId, BigDecimal amount) {
		Merchant merchant = merchantRepository.findById(merchantId)
				.orElseThrow(() -> new MerchantNotFoundException(merchantId));
		return cashbackCalculator.calculate(amount, merchant.cashbackRate());
	}
}
