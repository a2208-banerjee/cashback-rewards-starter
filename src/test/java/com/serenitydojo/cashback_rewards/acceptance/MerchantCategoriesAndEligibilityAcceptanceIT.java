package com.serenitydojo.cashback_rewards.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Merchant categories and eligibility")
class MerchantCategoriesAndEligibilityAcceptanceIT {

	@Autowired
	private MockMvc mockMvc;

	private final JsonMapper jsonMapper = new JsonMapper();

	@Nested
	@DisplayName("Must calculate cashback using the rate of the merchant's category, derived from its merchant category code (MCC)")
	class RateFromMerchantCategory {

		@ParameterizedTest(name = "MCC {0} earns {1} on a purchase of 100.00")
		@CsvSource({
				"5411, 2.00",
				"5422, 2.00",
				"5441, 2.00",
				"5451, 2.00",
				"5462, 2.00",
				"5541, 1.00",
				"5542, 1.00",
				"5812, 0.50"
		})
		@DisplayName("Groceries earn 2%, Fuel earns 1% and any other code earns the Default 0.5%")
		void purchaseEarnsTheRateOfTheCategoryOfItsMcc(String mcc, String expectedCashback) throws Exception {
			BigDecimal cashback = purchase("customer-" + mcc, "merchant-" + mcc, "\"" + mcc + "\"", "100.00");

			assertThat(cashback).isEqualByComparingTo(expectedCashback);
		}

		@Test
		@DisplayName("The one where a transaction carries no MCC, or one we don't recognise. It earns the Default 0.5%. It is not rejected and does not earn 0%")
		void missingOrUnrecognisedMccEarnsTheDefaultRate() throws Exception {
			BigDecimal withoutMcc = purchase("customer-no-mcc", "merchant-no-mcc", null, "100.00");
			BigDecimal withUnrecognisedMcc = purchase("customer-9999", "merchant-9999", "\"9999\"", "100.00");

			assertThat(withoutMcc).isEqualByComparingTo("0.50");
			assertThat(withUnrecognisedMcc).isEqualByComparingTo("0.50");
		}
	}

	private BigDecimal purchase(String customerId, String merchantId, String mccJson, String amount) throws Exception {
		String mccField = mccJson == null ? "" : ", \"mcc\": " + mccJson;
		String responseBody = mockMvc.perform(post("/purchases")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"customerId": "%s", "merchantId": "%s", "amount": %s%s}
								""".formatted(customerId, merchantId, amount, mccField)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		return jsonMapper.readTree(responseBody).get("cashback").decimalValue();
	}
}
