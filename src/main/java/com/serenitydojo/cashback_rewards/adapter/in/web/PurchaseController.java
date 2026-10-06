package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.PurchaseUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseController {

	private final PurchaseUseCase purchaseUseCase;

	public PurchaseController(PurchaseUseCase purchaseUseCase) {
		this.purchaseUseCase = purchaseUseCase;
	}

	@PostMapping("/purchases")
	@ResponseStatus(HttpStatus.CREATED)
	public PurchaseResponse purchase(@Valid @RequestBody PurchaseRequest request) {
		var cashback = purchaseUseCase.purchase(request.customerId(), request.merchantId(), request.amount());
		return new PurchaseResponse(cashback);
	}
}
