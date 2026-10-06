package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "purchases")
class PurchaseEntity {

	@Id
	private String purchaseId;

	private String customerId;

	private String merchantId;

	@Column(precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(precision = 19, scale = 2)
	private BigDecimal cashback;

	protected PurchaseEntity() {
	}

	PurchaseEntity(String purchaseId, String customerId, String merchantId, BigDecimal amount, BigDecimal cashback) {
		this.purchaseId = purchaseId;
		this.customerId = customerId;
		this.merchantId = merchantId;
		this.amount = amount;
		this.cashback = cashback;
	}

	String getPurchaseId() {
		return purchaseId;
	}

	String getCustomerId() {
		return customerId;
	}

	String getMerchantId() {
		return merchantId;
	}

	BigDecimal getAmount() {
		return amount;
	}

	BigDecimal getCashback() {
		return cashback;
	}
}
