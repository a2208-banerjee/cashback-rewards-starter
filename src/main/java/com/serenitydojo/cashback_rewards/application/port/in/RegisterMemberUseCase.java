package com.serenitydojo.cashback_rewards.application.port.in;

import java.time.ZoneId;

public interface RegisterMemberUseCase {

	void register(String memberId, ZoneId timezone);
}
