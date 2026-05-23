package gg.mmorealms.module.login_rewards.common.dto;

public enum DayStatus {
	CLAIMED,
	CLAIMABLE,
	LOCKED_PLAYTIME,
	LOCKED_COOLDOWN,
	LOCKED_INVENTORY,
	FUTURE;

	public boolean isClickable() {
		return this == CLAIMABLE;
	}
}
