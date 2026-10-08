package com.serenitydojo.cashback_rewards;

import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import com.serenitydojo.cashback_rewards.domain.service.CashbackReportCalculator;
import com.serenitydojo.cashback_rewards.domain.service.CategoryRateResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DomainConfiguration {

	@Bean
	CashbackCalculator cashbackCalculator() {
		return new CashbackCalculator();
	}

	@Bean
	CategoryRateResolver categoryRateResolver() {
		return new CategoryRateResolver();
	}

	@Bean
	CashbackReportCalculator cashbackReportCalculator() {
		return new CashbackReportCalculator();
	}
}
