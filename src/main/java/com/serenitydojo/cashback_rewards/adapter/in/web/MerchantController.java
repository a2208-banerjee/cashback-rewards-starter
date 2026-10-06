package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMerchantUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MerchantController {

	private final RegisterMerchantUseCase registerMerchantUseCase;

	public MerchantController(RegisterMerchantUseCase registerMerchantUseCase) {
		this.registerMerchantUseCase = registerMerchantUseCase;
	}

	@PostMapping("/merchants")
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@Valid @RequestBody RegisterMerchantRequest request) {
		registerMerchantUseCase.register(request.merchantId(), request.cashbackRate());
	}
}
