package com.serenitydojo.cashback_rewards.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Basic cashback calculation")
class BasicCashbackCalculationIT {

	@Autowired
	private MockMvc mockMvc;

	private final JsonMapper jsonMapper = new JsonMapper();

	@Nested
	@DisplayName("Rule: Must use the rate configured for the merchant where the purchase happened")
	class MerchantSpecificRate {

		@Test
		@DisplayName("The one where the same customer spends 100.00 at a 2% merchant and 100.00 at a 5% merchant")
		void sameCustomerEarnsDifferentCashbackAtDifferentMerchants() throws Exception {
			configureMerchant("coffee-corner", "0.02");
			configureMerchant("book-barn", "0.05");

			BigDecimal cashbackAtCoffeeCorner = purchase("customer-1", "coffee-corner", "100.00");
			BigDecimal cashbackAtBookBarn = purchase("customer-1", "book-barn", "100.00");

			assertThat(cashbackAtCoffeeCorner).isEqualByComparingTo("2.00");
			assertThat(cashbackAtBookBarn).isEqualByComparingTo("5.00");
		}
	}

	@Nested
	@DisplayName("Rule: Must apply the rate in force at the time of purchase")
	class RateInForceAtTimeOfPurchase {

		@Test
		@DisplayName("The one where a merchant changes its rate from 2% to 3% after a customer's purchase. The earlier purchase keeps its 2% cashback, and later purchases earn 3%")
		void earlierPurchaseKeepsOldRateAndLaterPurchasesEarnNewRate() throws Exception {
			configureMerchant("gadget-store", "0.02");

			JsonNode earlierPurchase = placePurchase("customer-2", "gadget-store", "100.00");

			changeMerchantRate("gadget-store", "0.03");

			JsonNode laterPurchase = placePurchase("customer-2", "gadget-store", "100.00");

			assertThat(laterPurchase.get("cashback").decimalValue()).isEqualByComparingTo("3.00");
			assertThat(recordedCashback(earlierPurchase.get("purchaseId").asString())).isEqualByComparingTo("2.00");
		}
	}

	private void changeMerchantRate(String merchantId, String cashbackRate) throws Exception {
		mockMvc.perform(put("/merchants/{merchantId}/cashback-rate", merchantId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"cashbackRate": %s}
								""".formatted(cashbackRate)))
				.andExpect(status().isOk());
	}

	private JsonNode placePurchase(String customerId, String merchantId, String amount) throws Exception {
		String responseBody = mockMvc.perform(post("/purchases")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"customerId": "%s", "merchantId": "%s", "amount": %s}
								""".formatted(customerId, merchantId, amount)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		return jsonMapper.readTree(responseBody);
	}

	private BigDecimal recordedCashback(String purchaseId) throws Exception {
		String responseBody = mockMvc.perform(get("/purchases/{purchaseId}", purchaseId))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		return jsonMapper.readTree(responseBody).get("cashback").decimalValue();
	}

	private void configureMerchant(String merchantId, String cashbackRate) throws Exception {
		mockMvc.perform(post("/merchants")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"merchantId": "%s", "cashbackRate": %s}
								""".formatted(merchantId, cashbackRate)))
				.andExpect(status().isCreated());
	}

	private BigDecimal purchase(String customerId, String merchantId, String amount) throws Exception {
		String responseBody = mockMvc.perform(post("/purchases")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"customerId": "%s", "merchantId": "%s", "amount": %s}
								""".formatted(customerId, merchantId, amount)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		return jsonMapper.readTree(responseBody).get("cashback").decimalValue();
	}
}
