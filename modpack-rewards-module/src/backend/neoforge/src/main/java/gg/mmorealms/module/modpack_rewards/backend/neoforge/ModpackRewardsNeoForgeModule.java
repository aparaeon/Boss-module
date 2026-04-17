package gg.mmorealms.module.modpack_rewards.backend.neoforge;

import gg.mmorealms.module.modpack_rewards.ModpackRewardsModuleBuildConstants;
import gg.mmorealms.module.modpack_rewards.backend.common.ModpackRewardsBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(ModpackRewardsModuleBuildConstants.ID)
public class ModpackRewardsNeoForgeModule extends ModpackRewardsBackendModule {
	public ModpackRewardsNeoForgeModule() {
		this.setup();
	}
}
