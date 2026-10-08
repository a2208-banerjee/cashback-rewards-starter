package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.domain.model.MonthlyCashbackReport;

import java.math.BigDecimal;
import java.util.List;

public record CashbackReportResponse(String month, String timezone, List<CashbackEntryResponse> entries,
		BigDecimal total) {

	static CashbackReportResponse from(MonthlyCashbackReport report) {
		List<CashbackEntryResponse> entries = report.entries().stream()
				.map(event -> CashbackEntryResponse.from(event, report.timezone()))
				.toList();
		return new CashbackReportResponse(report.month().toString(), report.timezone().getId(), entries,
				report.total());
	}
}
