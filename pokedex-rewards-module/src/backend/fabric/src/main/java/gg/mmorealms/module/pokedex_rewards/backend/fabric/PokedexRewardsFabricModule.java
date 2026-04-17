package gg.mmorealms.module.pokedex_rewards.backend.fabric;

import gg.mmorealms.module.pokedex_rewards.backend.common.PokedexRewardsBackendModule;
import net.fabricmc.api.ModInitializer;

public class PokedexRewardsFabricModule extends PokedexRewardsBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
