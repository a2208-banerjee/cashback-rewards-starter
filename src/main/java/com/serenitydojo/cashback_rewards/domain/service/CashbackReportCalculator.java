package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.model.CashbackEvent;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

public class CashbackReportCalculator {

	public List<CashbackEvent> eventsInMonth(List<CashbackEvent> events, ZoneId memberZone, YearMonth month) {
		return events.stream()
				.filter(event -> YearMonth.from(event.postedAt().atZone(memberZone)).equals(month))
				.sorted(Comparator.comparing(CashbackEvent::postedAt))
				.toList();
	}

	public BigDecimal total(List<CashbackEvent> events) {
		return events.stream()
				.map(CashbackEvent::cashback)
				.reduce(new BigDecimal("0.00"), BigDecimal::add);
	}
}
