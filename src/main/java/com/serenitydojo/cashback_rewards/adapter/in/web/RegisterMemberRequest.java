package com.serenitydojo.cashback_rewards.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

public record RegisterMemberRequest(@NotBlank String memberId, @NotNull ZoneId timezone) {
}
