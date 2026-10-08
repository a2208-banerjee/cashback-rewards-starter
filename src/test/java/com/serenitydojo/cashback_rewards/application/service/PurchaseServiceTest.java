package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import com.serenitydojo.cashback_rewards.domain.service.CategoryRateResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Purchase service")
class PurchaseServiceTest {

	private final Instant postedAt = Instant.parse("2026-03-04T10:15:00Z");

	@Nested
	@DisplayName("Must calculate cashback using the rate of the merchant's category, derived from its MCC")
	class RateFromMerchantCategory {

		@Test
		@DisplayName("The same customer spends 100.00 at a Groceries merchant (2%) and 100.00 at a Fuel merchant (1%)")
		void sameCustomerEarnsTheRateOfEachMerchantsCategory() {
			var service = new PurchaseService(new CategoryRateResolver(), new CashbackCalculator(), purchase -> { },
					Clock.systemUTC());

			BigDecimal cashbackAtGroceriesMerchant =
					service.purchase("customer-1", "coffee-corner", "5411", new BigDecimal("100.00"), postedAt);
			BigDecimal cashbackAtFuelMerchant =
					service.purchase("customer-1", "book-barn", "5541", new BigDecimal("100.00"), postedAt);

			assertThat(cashbackAtGroceriesMerchant).isEqualByComparingTo("2.00");
			assertThat(cashbackAtFuelMerchant).isEqualByComparingTo("1.00");
		}
	}

	@Nested
	@DisplayName("Must record each purchase with the cashback it earned and when it was posted")
	class RecordingPurchases {

		@Test
		@DisplayName("A 100.00 Groceries purchase is saved with 2.00 cashback, its customer, merchant and posting time")
		void purchaseIsSavedWithItsCashbackAndPostingTime() {
			List<Purchase> saved = new ArrayList<>();
			var service = new PurchaseService(new CategoryRateResolver(), new CashbackCalculator(), saved::add,
					Clock.systemUTC());

			service.purchase("customer-1", "coffee-corner", "5411", new BigDecimal("100.00"), postedAt);

			assertThat(saved).singleElement().satisfies(purchase -> {
				assertThat(purchase.purchaseId()).isNotBlank();
				assertThat(purchase.customerId()).isEqualTo("customer-1");
				assertThat(purchase.merchantId()).isEqualTo("coffee-corner");
				assertThat(purchase.amount()).isEqualByComparingTo("100.00");
				assertThat(purchase.cashback()).isEqualByComparingTo("2.00");
				assertThat(purchase.postedAt()).isEqualTo(postedAt);
			});
		}

		@Test
		@DisplayName("A purchase submitted without a posting time is posted at the current time")
		void purchaseWithoutPostingTimeIsPostedNow() {
			List<Purchase> saved = new ArrayList<>();
			Instant now = Instant.parse("2026-10-07T09:30:00Z");
			var service = new PurchaseService(new CategoryRateResolver(), new CashbackCalculator(), saved::add,
					Clock.fixed(now, ZoneOffset.UTC));

			service.purchase("customer-1", "coffee-corner", "5411", new BigDecimal("100.00"), null);

			assertThat(saved).singleElement().satisfies(purchase -> assertThat(purchase.postedAt()).isEqualTo(now));
		}
	}
}
