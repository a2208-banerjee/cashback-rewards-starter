package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Member;

public interface SaveMemberPort {

	void save(Member member);
}
