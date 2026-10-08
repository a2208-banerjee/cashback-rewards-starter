package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.domain.model.CashbackEvent;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record CashbackEntryResponse(String postedAt, String merchantId, BigDecimal cashback) {

	static CashbackEntryResponse from(CashbackEvent event, ZoneId memberZone) {
		String localPostedAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(event.postedAt().atZone(memberZone));
		return new CashbackEntryResponse(localPostedAt, event.merchantId(), event.cashback());
	}
}
