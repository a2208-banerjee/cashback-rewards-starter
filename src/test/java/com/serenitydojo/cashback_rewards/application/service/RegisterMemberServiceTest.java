package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.domain.exception.MemberAlreadyExistsException;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Register member service")
class RegisterMemberServiceTest {

	@Nested
	@DisplayName("Registering a member")
	class Registering {

		@Test
		@DisplayName("The member is saved with their local timezone")
		void memberIsSavedWithTheirTimezone() {
			List<Member> saved = new ArrayList<>();
			var service = new RegisterMemberService(memberId -> Optional.empty(), saved::add);

			service.register("tonga-member", ZoneId.of("Pacific/Tongatapu"));

			assertThat(saved).containsExactly(new Member("tonga-member", ZoneId.of("Pacific/Tongatapu")));
		}

		@Test
		@DisplayName("A member id that is already registered is rejected and the existing member is left unchanged")
		void duplicateMemberIsRejectedWithoutChangingTheExistingMember() {
			List<Member> saved = new ArrayList<>();
			var existing = new Member("tonga-member", ZoneId.of("Pacific/Tongatapu"));
			var service = new RegisterMemberService(memberId -> Optional.of(existing), saved::add);

			assertThatThrownBy(() -> service.register("tonga-member", ZoneId.of("Europe/London")))
					.isInstanceOf(MemberAlreadyExistsException.class)
					.hasMessageContaining("tonga-member");

			assertThat(saved).isEmpty();
		}
	}
}
