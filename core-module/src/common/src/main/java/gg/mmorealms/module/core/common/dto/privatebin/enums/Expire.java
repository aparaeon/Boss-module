package gg.mmorealms.module.core.common.dto.privatebin.enums;

import lombok.Getter;

@Getter
public enum Expire {

	FIVE_MINUTES("5min"),
	TEN_MINUTES("10min"),
	ONE_HOUR("1hour"),
	ONE_DAY("1day"),
	ONE_WEEK("1week"),
	ONE_MONTH("1month"),
	ONE_YEAR("1year"),
	NEVER("never");

	private final String expire;

	Expire(String expire) {
		this.expire = expire;
	}

}
