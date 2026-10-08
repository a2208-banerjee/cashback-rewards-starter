package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.PurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.SavePurchasePort;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import com.serenitydojo.cashback_rewards.domain.service.CategoryRateResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class PurchaseService implements PurchaseUseCase {

	private final CategoryRateResolver categoryRateResolver;
	private final CashbackCalculator cashbackCalculator;
	private final SavePurchasePort savePurchasePort;
	private final Clock clock;

	public PurchaseService(CategoryRateResolver categoryRateResolver, CashbackCalculator cashbackCalculator,
			SavePurchasePort savePurchasePort, Clock clock) {
		this.categoryRateResolver = categoryRateResolver;
		this.cashbackCalculator = cashbackCalculator;
		this.savePurchasePort = savePurchasePort;
		this.clock = clock;
	}

	@Override
	public BigDecimal purchase(String customerId, String merchantId, String mcc, BigDecimal amount, Instant postedAt) {
		BigDecimal cashback = cashbackCalculator.calculate(amount, categoryRateResolver.rateForMcc(mcc));
		Instant effectivePostedAt = postedAt != null ? postedAt : clock.instant();
		savePurchasePort.save(new Purchase(UUID.randomUUID().toString(), customerId, merchantId, amount, cashback,
				effectivePostedAt));
		return cashback;
	}
}
