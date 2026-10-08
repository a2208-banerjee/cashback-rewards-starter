package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "members")
class MemberEntity implements Persistable<String> {

	@Id
	private String memberId;

	private String timezone;

	@Transient
	private boolean newMember = true;

	protected MemberEntity() {
	}

	MemberEntity(String memberId, String timezone) {
		this.memberId = memberId;
		this.timezone = timezone;
	}

	String getMemberId() {
		return memberId;
	}

	String getTimezone() {
		return timezone;
	}

	@Override
	public String getId() {
		return memberId;
	}

	/**
	 * A member is only ever created, never updated. Reporting it as new makes Spring Data insert it, so the
	 * primary key rejects a duplicate instead of {@code save} silently overwriting the existing row.
	 */
	@Override
	public boolean isNew() {
		return newMember;
	}

	@PostLoad
	@PostPersist
	void markNotNew() {
		this.newMember = false;
	}
}
