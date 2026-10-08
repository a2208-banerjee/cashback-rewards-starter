package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.domain.exception.MemberAlreadyExistsException;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(MemberPersistenceAdapter.class)
@DisplayName("Member persistence adapter")
class MemberPersistenceAdapterTest {

	@Autowired
	private MemberPersistenceAdapter adapter;

	@Autowired
	private TestEntityManager entityManager;

	@Nested
	@DisplayName("Saving and finding members")
	class SaveAndFind {

		@Test
		@DisplayName("A saved member can be found again by its id with its local timezone")
		void savedMemberCanBeFoundByIdWithItsTimezone() {
			adapter.save(new Member("tonga-member", ZoneId.of("Pacific/Tongatapu")));
			entityManager.flush();
			entityManager.clear();

			var found = adapter.findById("tonga-member");

			assertThat(found).isPresent();
			assertThat(found.get().memberId()).isEqualTo("tonga-member");
			assertThat(found.get().timezone()).isEqualTo(ZoneId.of("Pacific/Tongatapu"));
		}

		@Test
		@DisplayName("Saving a member id that already exists fails instead of overwriting the stored member")
		void savingAnExistingMemberIdFailsInsteadOfOverwriting() {
			adapter.save(new Member("tonga-member", ZoneId.of("Pacific/Tongatapu")));
			entityManager.flush();
			entityManager.clear();

			assertThatThrownBy(() -> adapter.save(new Member("tonga-member", ZoneId.of("Europe/London"))))
					.isInstanceOf(MemberAlreadyExistsException.class)
					.hasMessageContaining("tonga-member");
		}

		@Test
		@DisplayName("A member that was never saved is not found")
		void unknownMemberIsNotFound() {
			assertThat(adapter.findById("nobody")).isEmpty();
		}
	}
}
