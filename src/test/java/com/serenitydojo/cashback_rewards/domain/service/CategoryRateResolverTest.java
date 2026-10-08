package com.serenitydojo.cashback_rewards.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Category rate resolver")
class CategoryRateResolverTest {

	private final CategoryRateResolver resolver = new CategoryRateResolver();

	@Nested
	@DisplayName("Must calculate cashback using the rate of the merchant's category, derived from its MCC")
	class RateFromMcc {

		@Test
		@DisplayName("MCC 5411 is Groceries and earns 2%")
		void groceriesMccEarnsTwoPercent() {
			BigDecimal rate = resolver.rateForMcc("5411");

			assertThat(rate).isEqualByComparingTo("0.02");
		}

		@Test
		@DisplayName("MCC 5422 is also Groceries and earns 2%")
		void otherGroceriesMccAlsoEarnsTwoPercent() {
			BigDecimal rate = resolver.rateForMcc("5422");

			assertThat(rate).isEqualByComparingTo("0.02");
		}

		@Test
		@DisplayName("MCC 5541 is Fuel and earns 1%")
		void fuelMccEarnsOnePercent() {
			BigDecimal rate = resolver.rateForMcc("5541");

			assertThat(rate).isEqualByComparingTo("0.01");
		}

		@ParameterizedTest(name = "MCC {0} earns {1}")
		@CsvSource({
				"5441, 0.02",
				"5451, 0.02",
				"5462, 0.02",
				"5542, 0.01"
		})
		@DisplayName("The remaining Groceries codes earn 2% and the remaining Fuel code earns 1%")
		void remainingGroceriesAndFuelCodesEarnTheirCategoryRate(String mcc, String expectedRate) {
			BigDecimal rate = resolver.rateForMcc(mcc);

			assertThat(rate).isEqualByComparingTo(expectedRate);
		}

		@Test
		@DisplayName("Any other MCC, such as 5812, earns the Default 0.5%")
		void otherMccEarnsTheDefaultRate() {
			BigDecimal rate = resolver.rateForMcc("5812");

			assertThat(rate).isEqualByComparingTo("0.005");
		}

		@Test
		@DisplayName("A transaction with no MCC earns the Default 0.5%")
		void missingMccEarnsTheDefaultRate() {
			BigDecimal rate = resolver.rateForMcc(null);

			assertThat(rate).isEqualByComparingTo("0.005");
		}
	}
}
