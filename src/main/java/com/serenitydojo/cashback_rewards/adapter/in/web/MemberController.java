package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMemberUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

	private final RegisterMemberUseCase registerMemberUseCase;

	public MemberController(RegisterMemberUseCase registerMemberUseCase) {
		this.registerMemberUseCase = registerMemberUseCase;
	}

	@PostMapping("/members")
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@Valid @RequestBody RegisterMemberRequest request) {
		registerMemberUseCase.register(request.memberId(), request.timezone());
	}
}
