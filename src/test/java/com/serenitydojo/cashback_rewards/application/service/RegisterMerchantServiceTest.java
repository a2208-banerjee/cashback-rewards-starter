package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Register merchant service")
class RegisterMerchantServiceTest {

	@Nested
	@DisplayName("Registering a partner merchant")
	class Registering {

		@Test
		@DisplayName("The merchant is saved with its configured cashback rate")
		void merchantIsSavedWithItsRate() {
			List<Merchant> saved = new ArrayList<>();
			var service = new RegisterMerchantService(saved::add);

			service.register("coffee-corner", new BigDecimal("0.02"));

			assertThat(saved).containsExactly(new Merchant("coffee-corner", new BigDecimal("0.02")));
		}
	}
}
