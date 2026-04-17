package gg.mmorealms.module.modpack_rewards.backend.fabric;

import gg.mmorealms.module.modpack_rewards.backend.common.ModpackRewardsBackendModule;
import net.fabricmc.api.ModInitializer;

public class ModpackRewardsFabricModule extends ModpackRewardsBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}