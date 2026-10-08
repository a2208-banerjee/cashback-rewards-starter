package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.CashbackReportUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.FindCustomerPurchasesPort;
import com.serenitydojo.cashback_rewards.application.port.out.FindMemberPort;
import com.serenitydojo.cashback_rewards.domain.exception.MemberNotFoundException;
import com.serenitydojo.cashback_rewards.domain.model.CashbackEvent;
import com.serenitydojo.cashback_rewards.domain.model.Member;
import com.serenitydojo.cashback_rewards.domain.model.MonthlyCashbackReport;
import com.serenitydojo.cashback_rewards.domain.service.CashbackReportCalculator;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class CashbackReportService implements CashbackReportUseCase {

	private final FindMemberPort findMemberPort;
	private final FindCustomerPurchasesPort findCustomerPurchasesPort;
	private final CashbackReportCalculator calculator;

	public CashbackReportService(FindMemberPort findMemberPort, FindCustomerPurchasesPort findCustomerPurchasesPort,
			CashbackReportCalculator calculator) {
		this.findMemberPort = findMemberPort;
		this.findCustomerPurchasesPort = findCustomerPurchasesPort;
		this.calculator = calculator;
	}

	@Override
	public MonthlyCashbackReport report(String memberId, YearMonth month) {
		Member member = findMemberPort.findById(memberId)
				.orElseThrow(() -> new MemberNotFoundException(memberId));
		List<CashbackEvent> events = findCustomerPurchasesPort.findByCustomerId(memberId).stream()
				.map(purchase -> new CashbackEvent(purchase.postedAt(), purchase.merchantId(), purchase.cashback()))
				.toList();
		List<CashbackEvent> entries = calculator.eventsInMonth(events, member.timezone(), month);
		return new MonthlyCashbackReport(month, member.timezone(), entries, calculator.total(entries));
	}
}
