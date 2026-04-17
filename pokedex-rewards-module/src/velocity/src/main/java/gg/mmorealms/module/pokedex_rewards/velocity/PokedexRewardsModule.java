package gg.mmorealms.module.pokedex_rewards.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.pokedex_rewards.PokedexRewardsModuleBuildConstants;
import gg.mmorealms.module.pokedex_rewards.common.PokedexRewardsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = PokedexRewardsModuleBuildConstants.ID,
		name = PokedexRewardsModuleBuildConstants.ID,
		version = PokedexRewardsModuleBuildConstants.VERSION,
		authors = {"ZeroDelusions"}
)
public class PokedexRewardsModule extends PokedexRewardsCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static PokedexRewardsModule instance;

	public PokedexRewardsModule() {
		PokedexRewardsModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}
