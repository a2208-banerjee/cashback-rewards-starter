package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.CashbackReportUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
public class CashbackReportController {

	private final CashbackReportUseCase cashbackReportUseCase;

	public CashbackReportController(CashbackReportUseCase cashbackReportUseCase) {
		this.cashbackReportUseCase = cashbackReportUseCase;
	}

	@GetMapping("/members/{memberId}/cashback-reports/{month}")
	public CashbackReportResponse monthlyReport(@PathVariable String memberId, @PathVariable YearMonth month) {
		return CashbackReportResponse.from(cashbackReportUseCase.report(memberId, month));
	}
}
