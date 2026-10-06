package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.FindPurchasePort;
import com.serenitydojo.cashback_rewards.application.port.out.SavePurchasePort;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PurchasePersistenceAdapter implements SavePurchasePort, FindPurchasePort {

	private final SpringDataPurchaseRepository repository;

	public PurchasePersistenceAdapter(SpringDataPurchaseRepository repository) {
		this.repository = repository;
	}

	@Override
	public void save(Purchase purchase) {
		repository.save(new PurchaseEntity(purchase.purchaseId(), purchase.customerId(), purchase.merchantId(),
				purchase.amount(), purchase.cashback()));
	}

	@Override
	public Optional<Purchase> findById(String purchaseId) {
		return repository.findById(purchaseId)
				.map(entity -> new Purchase(entity.getPurchaseId(), entity.getCustomerId(), entity.getMerchantId(),
						entity.getAmount(), entity.getCashback()));
	}
}
