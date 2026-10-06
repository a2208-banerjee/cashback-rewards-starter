package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "merchants")
class MerchantEntity {

	@Id
	private String merchantId;

	@Column(precision = 7, scale = 4)
	private BigDecimal cashbackRate;

	protected MerchantEntity() {
	}

	MerchantEntity(String merchantId, BigDecimal cashbackRate) {
		this.merchantId = merchantId;
		this.cashbackRate = cashbackRate;
	}

	String getMerchantId() {
		return merchantId;
	}

	BigDecimal getCashbackRate() {
		return cashbackRate;
	}
}
