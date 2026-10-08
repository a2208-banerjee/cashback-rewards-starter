package com.serenitydojo.cashback_rewards.domain.exception;

public class MemberNotFoundException extends RuntimeException {

	public MemberNotFoundException(String memberId) {
		super("Member not found: " + memberId);
	}
}
