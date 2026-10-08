package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.model.CashbackEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cashback report calculator")
class CashbackReportCalculatorTest {

	private final CashbackReportCalculator calculator = new CashbackReportCalculator();

	@Nested
	@DisplayName("Must report cashback for a single calendar month in the member's local timezone")
	class LocalCalendarMonth {

		@Test
		@DisplayName("An event at 11pm local on 31 March in UTC+13 is in March, and one at 00:30 on 1 April local is not")
		void eventsAreSelectedByTheMonthInTheMembersLocalTimezone() {
			var elevenPmLocalOn31March = new CashbackEvent(
					Instant.parse("2026-03-31T10:00:00Z"), "tonga-merchant", new BigDecimal("2.00"));
			var halfPastMidnightLocalOn1April = new CashbackEvent(
					Instant.parse("2026-03-31T11:30:00Z"), "tonga-merchant", new BigDecimal("10.00"));

			List<CashbackEvent> march = calculator.eventsInMonth(
					List.of(elevenPmLocalOn31March, halfPastMidnightLocalOn1April),
					ZoneId.of("Pacific/Tongatapu"),
					YearMonth.of(2026, 3));

			assertThat(march).containsExactly(elevenPmLocalOn31March);
		}

		@Test
		@DisplayName("The member's daylight saving offset in force on the day decides the month, not their winter offset")
		void daylightSavingOffsetDecidesTheMonthAtTheBoundary() {
			var elevenThirtyPmBritishSummerTimeOn31March = new CashbackEvent(
					Instant.parse("2026-03-31T22:30:00Z"), "london-merchant", new BigDecimal("2.00"));
			var halfPastMidnightBritishSummerTimeOn1April = new CashbackEvent(
					Instant.parse("2026-03-31T23:30:00Z"), "london-merchant", new BigDecimal("10.00"));

			List<CashbackEvent> march = calculator.eventsInMonth(
					List.of(elevenThirtyPmBritishSummerTimeOn31March, halfPastMidnightBritishSummerTimeOn1April),
					ZoneId.of("Europe/London"),
					YearMonth.of(2026, 3));

			assertThat(march).containsExactly(elevenThirtyPmBritishSummerTimeOn31March);
		}
	}

	@Nested
	@DisplayName("Must list the entries of the month oldest first")
	class EntryOrder {

		@Test
		@DisplayName("Events supplied out of order are reported by posting time, oldest first")
		void entriesAreOrderedByPostingTimeOldestFirst() {
			var twentieth = new CashbackEvent(Instant.parse("2026-03-20T09:00:00Z"), "fuel-stop", new BigDecimal("0.60"));
			var fourth = new CashbackEvent(Instant.parse("2026-03-04T09:00:00Z"), "grocery-mart", new BigDecimal("1.00"));
			var twelfth = new CashbackEvent(Instant.parse("2026-03-12T09:00:00Z"), "grocery-mart", new BigDecimal("-1.00"));

			List<CashbackEvent> march = calculator.eventsInMonth(
					List.of(twentieth, fourth, twelfth), ZoneId.of("UTC"), YearMonth.of(2026, 3));

			assertThat(march).containsExactly(fourth, twelfth, twentieth);
		}
	}

	@Nested
	@DisplayName("Must show the monthly total as the net sum of all entries")
	class NetTotal {

		@Test
		@DisplayName("12.40 earned and 2.00 clawed back gives a reported total of 10.40")
		void totalIsEarningsMinusClawbacks() {
			var earning = new CashbackEvent(Instant.parse("2026-03-04T10:00:00Z"), "grocery-mart", new BigDecimal("12.40"));
			var clawback = new CashbackEvent(Instant.parse("2026-03-20T10:00:00Z"), "grocery-mart", new BigDecimal("-2.00"));

			BigDecimal total = calculator.total(List.of(earning, clawback));

			assertThat(total).isEqualByComparingTo("10.40");
		}

		@Test
		@DisplayName("No activity in the month gives a total of exactly 0.00, with two decimal places")
		void totalWithNoEventsIsZeroWithTwoDecimalPlaces() {
			BigDecimal total = calculator.total(List.of());

			assertThat(total).hasToString("0.00");
		}
	}
}
