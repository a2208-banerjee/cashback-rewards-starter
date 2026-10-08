package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.FindMemberPort;
import com.serenitydojo.cashback_rewards.application.port.out.SaveMemberPort;
import com.serenitydojo.cashback_rewards.domain.exception.MemberAlreadyExistsException;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Optional;

@Component
public class MemberPersistenceAdapter implements SaveMemberPort, FindMemberPort {

	private final SpringDataMemberRepository repository;

	public MemberPersistenceAdapter(SpringDataMemberRepository repository) {
		this.repository = repository;
	}

	@Override
	public void save(Member member) {
		try {
			repository.saveAndFlush(new MemberEntity(member.memberId(), member.timezone().getId()));
		} catch (DataIntegrityViolationException exception) {
			throw new MemberAlreadyExistsException(member.memberId());
		}
	}

	@Override
	public Optional<Member> findById(String memberId) {
		return repository.findById(memberId)
				.map(entity -> new Member(entity.getMemberId(), ZoneId.of(entity.getTimezone())));
	}
}
