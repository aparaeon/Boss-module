package gg.mmorealms.module.pokedex_rewards.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.pokedex_rewards.PokedexRewardsModuleBuildConstants;

@Module(
		id = PokedexRewardsModuleBuildConstants.ID,
		version = PokedexRewardsModuleBuildConstants.VERSION,
		authors = {"ZeroDelusions", "Radu Voinea"},
		dependencies = PokedexRewardsModuleBuildConstants.DEPENDENCIES
)
public abstract class PokedexRewardsCommonModule implements CommonModule {
}
