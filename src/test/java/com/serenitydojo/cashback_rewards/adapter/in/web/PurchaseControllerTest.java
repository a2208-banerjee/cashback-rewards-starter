package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.PurchaseUseCase;
import com.serenitydojo.cashback_rewards.domain.exception.MerchantNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseController.class)
@DisplayName("Purchase controller")
class PurchaseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PurchaseUseCase purchaseUseCase;

	@Nested
	@DisplayName("POST /purchases")
	class MakePurchase {

		@Test
		@DisplayName("Responds 201 Created with the cashback earned on the purchase")
		void respondsCreatedWithCashbackEarned() throws Exception {
			given(purchaseUseCase.purchase("customer-1", "coffee-corner", new BigDecimal("100.00")))
					.willReturn(new BigDecimal("2.00"));

			mockMvc.perform(post("/purchases")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"customerId": "customer-1", "merchantId": "coffee-corner", "amount": 100.00}
									"""))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.cashback").value(2.00));
		}

		@Test
		@DisplayName("Responds 404 Not Found when the merchant is not a partner")
		void respondsNotFoundForNonPartnerMerchant() throws Exception {
			given(purchaseUseCase.purchase("customer-1", "unknown-shop", new BigDecimal("100.00")))
					.willThrow(new MerchantNotFoundException("unknown-shop"));

			mockMvc.perform(post("/purchases")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"customerId": "customer-1", "merchantId": "unknown-shop", "amount": 100.00}
									"""))
					.andExpect(status().isNotFound());
		}
	}
}
