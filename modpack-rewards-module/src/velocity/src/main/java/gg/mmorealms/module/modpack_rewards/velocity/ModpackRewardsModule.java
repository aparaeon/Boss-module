package gg.mmorealms.module.modpack_rewards.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.modpack_rewards.ModpackRewardsModuleBuildConstants;
import gg.mmorealms.module.modpack_rewards.common.ModpackRewardsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = ModpackRewardsModuleBuildConstants.ID,
		name = ModpackRewardsModuleBuildConstants.ID,
		version = ModpackRewardsModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class ModpackRewardsModule extends ModpackRewardsCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static ModpackRewardsModule instance;

	public ModpackRewardsModule() {
		ModpackRewardsModule.instance = this;
	}

	@Override
	public void onInit() {

	}

	@Override
	public void onEnable() {
	}
}
