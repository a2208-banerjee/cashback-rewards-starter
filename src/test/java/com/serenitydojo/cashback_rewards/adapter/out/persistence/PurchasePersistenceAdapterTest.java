package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.domain.model.Purchase;
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
@Import(PurchasePersistenceAdapter.class)
@DisplayName("Purchase persistence adapter")
class PurchasePersistenceAdapterTest {

	@Autowired
	private PurchasePersistenceAdapter adapter;

	@Autowired
	private TestEntityManager entityManager;

	@Nested
	@DisplayName("Saving and finding purchases")
	class SaveAndFind {

		@Test
		@DisplayName("A saved purchase can be found again by its id with the cashback it earned")
		void savedPurchaseCanBeFoundByIdWithItsCashback() {
			adapter.save(new Purchase("purchase-1", "customer-2", "gadget-store",
					new BigDecimal("100.00"), new BigDecimal("2.00")));
			entityManager.flush();
			entityManager.clear();

			var found = adapter.findById("purchase-1");

			assertThat(found).isPresent();
			assertThat(found.get().customerId()).isEqualTo("customer-2");
			assertThat(found.get().merchantId()).isEqualTo("gadget-store");
			assertThat(found.get().amount()).isEqualByComparingTo("100.00");
			assertThat(found.get().cashback()).isEqualByComparingTo("2.00");
		}
	}
}
