package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Change merchant rate service")
class ChangeMerchantRateServiceTest {

	@Nested
	@DisplayName("Changing a merchant's cashback rate")
	class ChangingTheRate {

		@Test
		@DisplayName("The merchant is saved with the new rate")
		void merchantIsSavedWithTheNewRate() {
			List<Merchant> saved = new ArrayList<>();
			var service = new ChangeMerchantRateService(
					merchantId -> Optional.of(new Merchant(merchantId, new BigDecimal("0.02"))),
					saved::add);

			service.changeRate("gadget-store", new BigDecimal("0.03"));

			assertThat(saved).containsExactly(new Merchant("gadget-store", new BigDecimal("0.03")));
		}
	}
}
