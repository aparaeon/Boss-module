package gg.mmorealms.module.pokedex_rewards.backend.common.dto.enums;


import gg.mmorealms.module.pokedex_rewards.backend.common.PokedexRewardsBackendModule;

public enum RewardStatus {
	LOCKED,
	AVAILABLE,
	CLAIMED;

	public String getDisplayName() {
		return switch (this) {
			case LOCKED -> PokedexRewardsBackendModule.instance().getConfig().lang.lcokedMilestone;
			case AVAILABLE -> PokedexRewardsBackendModule.instance().getConfig().lang.availableMilestone;
			case CLAIMED -> PokedexRewardsBackendModule.instance().getConfig().lang.claimedMilestone;
		};
	}
}
