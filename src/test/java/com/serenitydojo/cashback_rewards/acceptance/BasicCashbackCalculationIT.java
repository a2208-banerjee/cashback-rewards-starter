package com.serenitydojo.cashback_rewards.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
@DisplayName("Basic cashback calculation")
class BasicCashbackCalculationIT {

	@Autowired
	private MockMvc mockMvc;

	private final JsonMapper jsonMapper = new JsonMapper();

	@Nested
	@DisplayName("Rule: Must use the rate of the category of the merchant where the purchase happened")
	class MerchantSpecificRate {

		@Test
		@DisplayName("The one where the same customer spends 100.00 at a Groceries merchant (2%) and 100.00 at a Fuel merchant (1%)")
		void sameCustomerEarnsDifferentCashbackAtDifferentMerchants() throws Exception {
			BigDecimal cashbackAtGroceriesMerchant = purchase("customer-1", "coffee-corner", "5411", "100.00");
			BigDecimal cashbackAtFuelMerchant = purchase("customer-1", "book-barn", "5541", "100.00");

			assertThat(cashbackAtGroceriesMerchant).isEqualByComparingTo("2.00");
			assertThat(cashbackAtFuelMerchant).isEqualByComparingTo("1.00");
		}
	}

	private BigDecimal purchase(String customerId, String merchantId, String mcc, String amount) throws Exception {
		String responseBody = mockMvc.perform(post("/purchases")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"customerId": "%s", "merchantId": "%s", "mcc": "%s", "amount": %s}
								""".formatted(customerId, merchantId, mcc, amount)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		return jsonMapper.readTree(responseBody).get("cashback").decimalValue();
	}
}
