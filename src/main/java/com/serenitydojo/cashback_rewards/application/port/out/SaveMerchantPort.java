package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;

public interface SaveMerchantPort {

	void save(Merchant merchant);
}
