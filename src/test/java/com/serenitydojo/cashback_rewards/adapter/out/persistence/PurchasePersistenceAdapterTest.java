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
import java.time.Instant;

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
					new BigDecimal("100.00"), new BigDecimal("2.00"), Instant.parse("2026-03-04T10:15:00Z")));
			entityManager.flush();
			entityManager.clear();

			var found = adapter.findById("purchase-1");

			assertThat(found).isPresent();
			assertThat(found.get().customerId()).isEqualTo("customer-2");
			assertThat(found.get().merchantId()).isEqualTo("gadget-store");
			assertThat(found.get().amount()).isEqualByComparingTo("100.00");
			assertThat(found.get().cashback()).isEqualByComparingTo("2.00");
		}

		@Test
		@DisplayName("A saved purchase keeps the exact instant at which it was posted")
		void savedPurchaseKeepsItsPostingInstant() {
			adapter.save(new Purchase("purchase-2", "customer-2", "gadget-store",
					new BigDecimal("100.00"), new BigDecimal("2.00"), Instant.parse("2026-03-31T10:00:00Z")));
			entityManager.flush();
			entityManager.clear();

			var found = adapter.findById("purchase-2");

			assertThat(found).isPresent();
			assertThat(found.get().postedAt()).isEqualTo(Instant.parse("2026-03-31T10:00:00Z"));
		}

		@Test
		@DisplayName("Finding by customer returns only that customer's purchases")
		void purchasesCanBeFoundByCustomerWithoutOtherCustomersPurchases() {
			adapter.save(new Purchase("purchase-a1", "customer-a", "gadget-store",
					new BigDecimal("100.00"), new BigDecimal("2.00"), Instant.parse("2026-03-01T09:00:00Z")));
			adapter.save(new Purchase("purchase-a2", "customer-a", "gadget-store",
					new BigDecimal("200.00"), new BigDecimal("4.00"), Instant.parse("2026-03-02T09:00:00Z")));
			adapter.save(new Purchase("purchase-b1", "customer-b", "gadget-store",
					new BigDecimal("300.00"), new BigDecimal("6.00"), Instant.parse("2026-03-03T09:00:00Z")));
			entityManager.flush();
			entityManager.clear();

			var purchases = adapter.findByCustomerId("customer-a");

			assertThat(purchases).extracting(Purchase::purchaseId)
					.containsExactlyInAnyOrder("purchase-a1", "purchase-a2");
		}
	}
}
