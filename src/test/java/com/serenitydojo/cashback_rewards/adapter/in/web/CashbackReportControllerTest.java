package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.CashbackReportUseCase;
import com.serenitydojo.cashback_rewards.domain.exception.MemberNotFoundException;
import com.serenitydojo.cashback_rewards.domain.model.CashbackEvent;
import com.serenitydojo.cashback_rewards.domain.model.MonthlyCashbackReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CashbackReportController.class)
@DisplayName("Cashback report controller")
class CashbackReportControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CashbackReportUseCase cashbackReportUseCase;

	@Nested
	@DisplayName("GET /members/{memberId}/cashback-reports/{month}")
	class GetMonthlyReport {

		@Test
		@DisplayName("Responds 200 with the month, timezone, entries at the member's local offset, and the total")
		void respondsWithTheReportInTheMembersLocalTime() throws Exception {
			given(cashbackReportUseCase.report("tonga-member", YearMonth.of(2026, 3)))
					.willReturn(new MonthlyCashbackReport(
							YearMonth.of(2026, 3),
							ZoneId.of("Pacific/Tongatapu"),
							List.of(new CashbackEvent(Instant.parse("2026-03-31T10:00:00Z"), "gadget-store",
									new BigDecimal("2.00"))),
							new BigDecimal("2.00")));

			mockMvc.perform(get("/members/{memberId}/cashback-reports/{month}", "tonga-member", "2026-03"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.month").value("2026-03"))
					.andExpect(jsonPath("$.timezone").value("Pacific/Tongatapu"))
					.andExpect(jsonPath("$.entries.length()").value(1))
					.andExpect(jsonPath("$.entries[0].postedAt").value("2026-03-31T23:00:00+13:00"))
					.andExpect(jsonPath("$.entries[0].merchantId").value("gadget-store"))
					.andExpect(jsonPath("$.entries[0].cashback").value(2.00))
					.andExpect(jsonPath("$.total").value(2.00));
		}

		@ParameterizedTest(name = "month \"{0}\"")
		@ValueSource(strings = { "2026-13", "2026-3", "march", "2026" })
		@DisplayName("Responds 400 Bad Request when the month is not a valid YYYY-MM value")
		void rejectsMalformedMonth(String month) throws Exception {
			mockMvc.perform(get("/members/{memberId}/cashback-reports/{month}", "tonga-member", month))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(cashbackReportUseCase);
		}

		@Test
		@DisplayName("Explains a malformed month with a problem+json 400 body")
		void explainsMalformedMonthWithProblemDetails() throws Exception {
			mockMvc.perform(get("/members/{memberId}/cashback-reports/{month}", "tonga-member", "2026-13"))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
					.andExpect(jsonPath("$.status").value(400))
					.andExpect(jsonPath("$.title").value("Bad Request"));
		}

		@Test
		@DisplayName("Explains a 404 with a problem+json body naming the missing member")
		void explainsNotFoundWithProblemDetails() throws Exception {
			given(cashbackReportUseCase.report("nobody", YearMonth.of(2026, 3)))
					.willThrow(new MemberNotFoundException("nobody"));

			mockMvc.perform(get("/members/{memberId}/cashback-reports/{month}", "nobody", "2026-03"))
					.andExpect(status().isNotFound())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
					.andExpect(jsonPath("$.status").value(404))
					.andExpect(jsonPath("$.title").value("Member not found"))
					.andExpect(jsonPath("$.detail").value("Member not found: nobody"));
		}

		@Test
		@DisplayName("Responds 404 Not Found when the member does not exist")
		void respondsNotFoundForUnknownMember() throws Exception {
			given(cashbackReportUseCase.report("nobody", YearMonth.of(2026, 3)))
					.willThrow(new MemberNotFoundException("nobody"));

			mockMvc.perform(get("/members/{memberId}/cashback-reports/{month}", "nobody", "2026-03"))
					.andExpect(status().isNotFound());
		}
	}
}
