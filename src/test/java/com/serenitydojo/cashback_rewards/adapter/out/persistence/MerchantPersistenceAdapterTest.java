package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MerchantPersistenceAdapter.class)
@DisplayName("Merchant persistence adapter")
class MerchantPersistenceAdapterTest {

	@Autowired
	private MerchantPersistenceAdapter adapter;

	@Autowired
	private TestEntityManager entityManager;

	@Nested
	@DisplayName("Saving and finding merchants")
	class SaveAndFind {

		@Test
		@DisplayName("A saved merchant can be found again by its id with its configured rate")
		void savedMerchantCanBeFoundById() {
			adapter.save(new Merchant("coffee-corner", new BigDecimal("0.02")));
			entityManager.flush();
			entityManager.clear();

			var found = adapter.findById("coffee-corner");

			assertThat(found).isPresent();
			assertThat(found.get().merchantId()).isEqualTo("coffee-corner");
			assertThat(found.get().cashbackRate()).isEqualByComparingTo("0.02");
		}

		@Test
		@DisplayName("A fractional rate such as 1.5% is stored without losing precision")
		void fractionalRateIsStoredExactly() {
			adapter.save(new Merchant("book-barn", new BigDecimal("0.015")));
			entityManager.flush();
			entityManager.clear();

			var found = adapter.findById("book-barn");

			assertThat(found).isPresent();
			assertThat(found.get().cashbackRate()).isEqualByComparingTo("0.015");
		}
	}
}
