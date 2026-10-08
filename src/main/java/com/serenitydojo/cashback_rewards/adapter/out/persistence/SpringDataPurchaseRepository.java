package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataPurchaseRepository extends JpaRepository<PurchaseEntity, String> {

	List<PurchaseEntity> findByCustomerId(String customerId);
}
