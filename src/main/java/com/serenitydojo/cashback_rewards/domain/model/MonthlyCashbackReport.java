package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

public record MonthlyCashbackReport(YearMonth month, ZoneId timezone, List<CashbackEvent> entries, BigDecimal total) {
}
