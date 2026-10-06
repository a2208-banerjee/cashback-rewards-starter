package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.domain.exception.MerchantNotFoundException;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Purchase service")
class PurchaseServiceTest {

	@Nested
	@DisplayName("Must use the rate configured for the merchant where the purchase happened")
	class MerchantSpecificRate {

		@Test
		@DisplayName("The one where the same customer spends 100.00 at a 2% merchant and 100.00 at a 5% merchant")
		void sameCustomerEarnsDifferentCashbackAtDifferentMerchants() {
			var merchants = Map.of(
					"coffee-corner", new Merchant("coffee-corner", new BigDecimal("0.02")),
					"book-barn", new Merchant("book-barn", new BigDecimal("0.05")));
			var service = new PurchaseService(id -> Optional.ofNullable(merchants.get(id)),new CashbackCalculator());

			BigDecimal cashbackAtCoffeeCorner = service.purchase("customer-1", "coffee-corner", new BigDecimal("100.00"));
			BigDecimal cashbackAtBookBarn = service.purchase("customer-1", "book-barn", new BigDecimal("100.00"));

			assertThat(cashbackAtCoffeeCorner).isEqualByComparingTo("2.00");
			assertThat(cashbackAtBookBarn).isEqualByComparingTo("5.00");
		}
	}

	@Nested
	@DisplayName("Must reject purchases at merchants that are not partners")
	class NonPartnerMerchant {

		@Test
		@DisplayName("The one where a purchase is submitted for a merchant with no configured rate")
		void purchaseAtUnknownMerchantIsRejected() {
			var service = new PurchaseService(merchantId -> Optional.empty(), new CashbackCalculator());

			assertThatThrownBy(() -> service.purchase("customer-1", "unknown-shop", new BigDecimal("100.00")))
					.isInstanceOf(MerchantNotFoundException.class)
					.hasMessageContaining("unknown-shop");
		}
	}
}
