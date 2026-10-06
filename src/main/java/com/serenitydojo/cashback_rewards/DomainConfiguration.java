package com.serenitydojo.cashback_rewards;

import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DomainConfiguration {

	@Bean
	CashbackCalculator cashbackCalculator() {
		return new CashbackCalculator();
	}
}
