package com.serenitydojo.cashback_rewards.adapter.in.web;

import java.math.BigDecimal;

public record RegisterMerchantRequest(String merchantId, BigDecimal cashbackRate) {
}
