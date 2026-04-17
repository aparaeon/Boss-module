package gg.mmorealms.module.modpack_rewards.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.modpack_rewards.ModpackRewardsModuleBuildConstants;

@Module(
		id = ModpackRewardsModuleBuildConstants.ID,
		version = ModpackRewardsModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = ModpackRewardsModuleBuildConstants.DEPENDENCIES
)
public abstract class ModpackRewardsCommonModule implements CommonModule {
}
