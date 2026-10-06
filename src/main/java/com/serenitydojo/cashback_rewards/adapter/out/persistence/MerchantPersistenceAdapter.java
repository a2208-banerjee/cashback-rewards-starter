package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.application.port.out.SaveMerchantPort;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MerchantPersistenceAdapter implements MerchantRepository, SaveMerchantPort {

	private final SpringDataMerchantRepository repository;

	public MerchantPersistenceAdapter(SpringDataMerchantRepository repository) {
		this.repository = repository;
	}

	@Override
	public void save(Merchant merchant) {
		repository.save(new MerchantEntity(merchant.merchantId(), merchant.cashbackRate()));
	}

	@Override
	public Optional<Merchant> findById(String merchantId) {
		return repository.findById(merchantId)
				.map(entity -> new Merchant(entity.getMerchantId(), entity.getCashbackRate()));
	}
}
