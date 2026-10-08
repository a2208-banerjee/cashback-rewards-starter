package com.serenitydojo.cashback_rewards;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class ClockConfiguration {

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}
}
