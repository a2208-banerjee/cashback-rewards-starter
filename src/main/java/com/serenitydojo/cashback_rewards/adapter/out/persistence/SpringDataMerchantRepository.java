package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataMerchantRepository extends JpaRepository<MerchantEntity, String> {
}
