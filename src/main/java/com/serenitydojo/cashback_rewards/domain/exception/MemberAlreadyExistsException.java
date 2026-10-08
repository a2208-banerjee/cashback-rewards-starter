package com.serenitydojo.cashback_rewards.domain.exception;

public class MemberAlreadyExistsException extends RuntimeException {

	public MemberAlreadyExistsException(String memberId) {
		super("Member already exists: " + memberId);
	}
}
