package com.serenitydojo.cashback_rewards.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Cashback monthly report")
class CashbackMonthlyReportAcceptanceIT {

	@Autowired
	private MockMvc mockMvc;

	private final JsonMapper jsonMapper = new JsonMapper();

	@Nested
	@DisplayName("Must report cashback for a single calendar month in the member's local timezone")
	class SingleCalendarMonthInLocalTimezone {

		@Test
		@DisplayName("The one where a member requests their report for March 2026 — the report contains every cashback event with a posting date between 1 March 00:00 and 31 March 23:59 in the member's local timezone")
		void reportContainsEveryEventPostedWithinTheRequestedMonth() throws Exception {
			registerMember("march-member", "UTC");

			purchase("march-member", "march-merchant", "300.00", "2026-02-28T23:59:00Z");
			purchase("march-member", "march-merchant", "100.00", "2026-03-01T00:00:00Z");
			purchase("march-member", "march-merchant", "200.00", "2026-03-31T23:59:00Z");
			purchase("march-member", "march-merchant", "400.00", "2026-04-01T00:00:00Z");

			JsonNode marchReport = requestReport("march-member", "2026-03");

			assertThat(marchReport.get("month").asString()).isEqualTo("2026-03");
			assertThat(marchReport.get("timezone").asString()).isEqualTo("UTC");
			assertThat(postedAtOfEntries(marchReport)).containsExactly(
					OffsetDateTime.parse("2026-03-01T00:00:00Z"),
					OffsetDateTime.parse("2026-03-31T23:59:00Z"));
			assertThat(cashbackOfEntries(marchReport))
					.usingElementComparator(BigDecimal::compareTo)
					.containsExactly(new BigDecimal("2.00"), new BigDecimal("4.00"));
		}

		@Test
		@DisplayName("The one where a member in UTC+13 has a transaction that posts at 11pm local on 31 March — it appears in the March report, not April, because we use the member's local month")
		void eventsAreAssignedToTheMonthInTheMembersLocalTimezone() throws Exception {
			registerMember("tonga-member", "Pacific/Tongatapu");

			// 23:00 on 31 March local time (UTC+13)
			purchase("tonga-member", "tonga-merchant", "100.00", "2026-03-31T10:00:00Z");
			// 00:30 on 1 April local time (UTC+13), still 31 March in UTC
			purchase("tonga-member", "tonga-merchant", "500.00", "2026-03-31T11:30:00Z");

			JsonNode marchReport = requestReport("tonga-member", "2026-03");
			JsonNode aprilReport = requestReport("tonga-member", "2026-04");

			assertThat(marchReport.get("timezone").asString()).isEqualTo("Pacific/Tongatapu");
			assertThat(postedAtOfEntries(marchReport))
					.containsExactly(OffsetDateTime.parse("2026-03-31T23:00:00+13:00"));
			assertThat(cashbackOfEntries(marchReport))
					.usingElementComparator(BigDecimal::compareTo)
					.containsExactly(new BigDecimal("2.00"));
			assertThat(postedAtOfEntries(aprilReport))
					.containsExactly(OffsetDateTime.parse("2026-04-01T00:30:00+13:00"));
			assertThat(cashbackOfEntries(aprilReport))
					.usingElementComparator(BigDecimal::compareTo)
					.containsExactly(new BigDecimal("10.00"));
		}
	}

	private void registerMember(String memberId, String timezone) throws Exception {
		mockMvc.perform(post("/members")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"memberId": "%s", "timezone": "%s"}
								""".formatted(memberId, timezone)))
				.andExpect(status().isCreated());
	}

	/** Purchases are made at a Groceries merchant (MCC 5411), which earns 2%. */
	private void purchase(String customerId, String merchantId, String amount, String postedAt) throws Exception {
		mockMvc.perform(post("/purchases")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"customerId": "%s", "merchantId": "%s", "mcc": "5411", "amount": %s, "postedAt": "%s"}
								""".formatted(customerId, merchantId, amount, postedAt)))
				.andExpect(status().isCreated());
	}

	private JsonNode requestReport(String memberId, String month) throws Exception {
		String responseBody = mockMvc.perform(get("/members/{memberId}/cashback-reports/{month}", memberId, month))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		return jsonMapper.readTree(responseBody);
	}

	private List<OffsetDateTime> postedAtOfEntries(JsonNode report) {
		List<OffsetDateTime> postedAt = new ArrayList<>();
		for (JsonNode entry : report.get("entries")) {
			postedAt.add(OffsetDateTime.parse(entry.get("postedAt").asString()));
		}
		return postedAt;
	}

	private List<BigDecimal> cashbackOfEntries(JsonNode report) {
		List<BigDecimal> cashback = new ArrayList<>();
		for (JsonNode entry : report.get("entries")) {
			cashback.add(entry.get("cashback").decimalValue());
		}
		return cashback;
	}
}
