package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.ChangeMerchantRateUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.application.port.out.SaveMerchantPort;
import com.serenitydojo.cashback_rewards.domain.exception.MerchantNotFoundException;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ChangeMerchantRateService implements ChangeMerchantRateUseCase {

	private final MerchantRepository merchantRepository;
	private final SaveMerchantPort saveMerchantPort;

	public ChangeMerchantRateService(MerchantRepository merchantRepository, SaveMerchantPort saveMerchantPort) {
		this.merchantRepository = merchantRepository;
		this.saveMerchantPort = saveMerchantPort;
	}

	@Override
	public void changeRate(String merchantId, BigDecimal newCashbackRate) {
		Merchant merchant = merchantRepository.findById(merchantId)
				.orElseThrow(() -> new MerchantNotFoundException(merchantId));
		saveMerchantPort.save(new Merchant(merchant.merchantId(), newCashbackRate));
	}
}
