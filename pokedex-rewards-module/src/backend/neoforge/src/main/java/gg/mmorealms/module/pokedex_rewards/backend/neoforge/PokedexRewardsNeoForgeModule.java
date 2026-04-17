package gg.mmorealms.module.pokedex_rewards.backend.neoforge;

import gg.mmorealms.module.pokedex_rewards.PokedexRewardsModuleBuildConstants;
import gg.mmorealms.module.pokedex_rewards.backend.common.PokedexRewardsBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(PokedexRewardsModuleBuildConstants.ID)
public class PokedexRewardsNeoForgeModule extends PokedexRewardsBackendModule {
	public PokedexRewardsNeoForgeModule() {
		this.setup();
	}
}
