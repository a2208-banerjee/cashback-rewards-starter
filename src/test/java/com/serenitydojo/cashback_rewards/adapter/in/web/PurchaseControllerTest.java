package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.PurchaseUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
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
			given(purchaseUseCase.purchase("customer-1", "coffee-corner", "5411", new BigDecimal("100.00"),
					Instant.parse("2026-03-04T10:15:00Z")))
					.willReturn(new BigDecimal("2.00"));

			mockMvc.perform(post("/purchases")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"customerId": "customer-1", "merchantId": "coffee-corner", "mcc": "5411", "amount": 100.00,
									 "postedAt": "2026-03-04T10:15:00Z"}
									"""))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.cashback").value(2.00));
		}

		@Test
		@DisplayName("Responds 400 Bad Request when the customer id is blank")
		void rejectsBlankCustomerId() throws Exception {
			mockMvc.perform(post("/purchases")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"customerId": " ", "merchantId": "coffee-corner", "mcc": "5411", "amount": 100.00}
									"""))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(purchaseUseCase);
		}

		@Test
		@DisplayName("Responds 400 Bad Request when the merchant id is missing")
		void rejectsMissingMerchantId() throws Exception {
			mockMvc.perform(post("/purchases")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"customerId": "customer-1", "mcc": "5411", "amount": 100.00}
									"""))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(purchaseUseCase);
		}

		@Test
		@DisplayName("Responds 400 Bad Request when the amount is missing")
		void rejectsMissingAmount() throws Exception {
			mockMvc.perform(post("/purchases")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"customerId": "customer-1", "merchantId": "coffee-corner", "mcc": "5411"}
									"""))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(purchaseUseCase);
		}
	}
}
