package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMemberUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.FindMemberPort;
import com.serenitydojo.cashback_rewards.application.port.out.SaveMemberPort;
import com.serenitydojo.cashback_rewards.domain.exception.MemberAlreadyExistsException;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
public class RegisterMemberService implements RegisterMemberUseCase {

	private final FindMemberPort findMemberPort;
	private final SaveMemberPort saveMemberPort;

	public RegisterMemberService(FindMemberPort findMemberPort, SaveMemberPort saveMemberPort) {
		this.findMemberPort = findMemberPort;
		this.saveMemberPort = saveMemberPort;
	}

	@Override
	public void register(String memberId, ZoneId timezone) {
		if (findMemberPort.findById(memberId).isPresent()) {
			throw new MemberAlreadyExistsException(memberId);
		}
		saveMemberPort.save(new Member(memberId, timezone));
	}
}
