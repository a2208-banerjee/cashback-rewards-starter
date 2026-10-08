package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.domain.exception.MemberNotFoundException;
import com.serenitydojo.cashback_rewards.domain.model.CashbackEvent;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import com.serenitydojo.cashback_rewards.domain.model.MonthlyCashbackReport;
import com.serenitydojo.cashback_rewards.domain.model.Purchase;
import com.serenitydojo.cashback_rewards.domain.service.CashbackReportCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Cashback report service")
class CashbackReportServiceTest {

	@Nested
	@DisplayName("Must report cashback for a single calendar month in the member's local timezone")
	class SingleCalendarMonthInLocalTimezone {

		@Test
		@DisplayName("The member's purchases are filtered to the requested month in their timezone and totalled")
		void reportContainsOnlyTheEventsOfTheMonthInTheMembersTimezone() {
			var member = new Member("tonga-member", ZoneId.of("Pacific/Tongatapu"));
			var elevenPmLocalOn31March = new Purchase("p1", "tonga-member", "gadget-store",
					new BigDecimal("100.00"), new BigDecimal("2.00"), Instant.parse("2026-03-31T10:00:00Z"));
			var halfPastMidnightLocalOn1April = new Purchase("p2", "tonga-member", "gadget-store",
					new BigDecimal("500.00"), new BigDecimal("10.00"), Instant.parse("2026-03-31T11:30:00Z"));
			var service = new CashbackReportService(
					memberId -> Optional.of(member),
					customerId -> List.of(elevenPmLocalOn31March, halfPastMidnightLocalOn1April),
					new CashbackReportCalculator());

			MonthlyCashbackReport report = service.report("tonga-member", YearMonth.of(2026, 3));

			assertThat(report.month()).isEqualTo(YearMonth.of(2026, 3));
			assertThat(report.timezone()).isEqualTo(ZoneId.of("Pacific/Tongatapu"));
			assertThat(report.entries()).extracting(CashbackEvent::postedAt)
					.containsExactly(Instant.parse("2026-03-31T10:00:00Z"));
			assertThat(report.total()).isEqualByComparingTo("2.00");
		}

		@Test
		@DisplayName("A report for a member that does not exist is rejected with a domain error")
		void reportForUnknownMemberIsRejected() {
			var service = new CashbackReportService(
					memberId -> Optional.empty(),
					customerId -> List.of(),
					new CashbackReportCalculator());

			assertThatThrownBy(() -> service.report("nobody", YearMonth.of(2026, 3)))
					.isInstanceOf(MemberNotFoundException.class)
					.hasMessageContaining("nobody");
		}
	}
}
