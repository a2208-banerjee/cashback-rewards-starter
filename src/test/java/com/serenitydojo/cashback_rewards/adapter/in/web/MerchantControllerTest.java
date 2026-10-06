package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMerchantUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MerchantController.class)
@DisplayName("Merchant controller")
class MerchantControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RegisterMerchantUseCase registerMerchantUseCase;

	@Nested
	@DisplayName("POST /merchants")
	class RegisterMerchant {

		@Test
		@DisplayName("Registers the merchant with its rate and responds 201 Created")
		void registersMerchantAndRespondsCreated() throws Exception {
			mockMvc.perform(post("/merchants")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"merchantId": "coffee-corner", "cashbackRate": 0.02}
									"""))
					.andExpect(status().isCreated());

			verify(registerMerchantUseCase).register("coffee-corner", new BigDecimal("0.02"));
		}
	}
}
