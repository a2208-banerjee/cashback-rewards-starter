package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMemberUseCase;
import com.serenitydojo.cashback_rewards.domain.exception.MemberAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@DisplayName("Member controller")
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RegisterMemberUseCase registerMemberUseCase;

	@Nested
	@DisplayName("POST /members")
	class RegisterMember {

		@Test
		@DisplayName("Registers the member with their timezone and responds 201 Created")
		void registersMemberAndRespondsCreated() throws Exception {
			mockMvc.perform(post("/members")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"memberId": "tonga-member", "timezone": "Pacific/Tongatapu"}
									"""))
					.andExpect(status().isCreated());

			verify(registerMemberUseCase).register("tonga-member", ZoneId.of("Pacific/Tongatapu"));
		}

		@Test
		@DisplayName("Responds 409 Conflict with a problem+json body when the member is already registered")
		void respondsConflictForDuplicateMember() throws Exception {
			doThrow(new MemberAlreadyExistsException("tonga-member"))
					.when(registerMemberUseCase).register("tonga-member", ZoneId.of("Pacific/Tongatapu"));

			mockMvc.perform(post("/members")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"memberId": "tonga-member", "timezone": "Pacific/Tongatapu"}
									"""))
					.andExpect(status().isConflict())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
					.andExpect(jsonPath("$.status").value(409))
					.andExpect(jsonPath("$.title").value("Member already exists"))
					.andExpect(jsonPath("$.detail").value("Member already exists: tonga-member"));
		}

		@Test
		@DisplayName("Responds 400 Bad Request when the member id is blank")
		void rejectsBlankMemberId() throws Exception {
			mockMvc.perform(post("/members")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"memberId": " ", "timezone": "Pacific/Tongatapu"}
									"""))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(registerMemberUseCase);
		}

		@Test
		@DisplayName("Explains a failed validation with a problem+json 400 body")
		void explainsValidationFailureWithProblemDetails() throws Exception {
			mockMvc.perform(post("/members")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"memberId": " ", "timezone": "Pacific/Tongatapu"}
									"""))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
					.andExpect(jsonPath("$.status").value(400))
					.andExpect(jsonPath("$.title").value("Bad Request"));
		}

		@Test
		@DisplayName("Explains an unreadable timezone with a problem+json 400 body that leaks no parsing internals")
		void explainsUnreadableTimezoneWithoutLeakingInternals() throws Exception {
			mockMvc.perform(post("/members")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"memberId": "tonga-member", "timezone": "Mars/Olympus"}
									"""))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
					.andExpect(jsonPath("$.status").value(400))
					.andExpect(content().string(not(containsString("ZoneRulesException"))))
					.andExpect(content().string(not(containsString("java.time"))))
					.andExpect(content().string(not(containsString("jackson"))));
		}

		@Test
		@DisplayName("Responds 400 Bad Request when the timezone is missing")
		void rejectsMissingTimezone() throws Exception {
			mockMvc.perform(post("/members")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"memberId": "tonga-member"}
									"""))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(registerMemberUseCase);
		}

		@Test
		@DisplayName("Responds 400 Bad Request when the timezone is not a real timezone")
		void rejectsUnknownTimezone() throws Exception {
			mockMvc.perform(post("/members")
							.contentType(MediaType.APPLICATION_JSON)
							.content("""
									{"memberId": "tonga-member", "timezone": "Mars/Olympus"}
									"""))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(registerMemberUseCase);
		}
	}
}
