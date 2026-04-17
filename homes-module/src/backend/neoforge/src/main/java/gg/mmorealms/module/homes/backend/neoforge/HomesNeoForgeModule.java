package gg.mmorealms.module.homes.backend.neoforge;

import gg.mmorealms.module.homes.HomesModuleBuildConstants;
import gg.mmorealms.module.homes.backend.common.HomesBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(HomesModuleBuildConstants.ID)
public class HomesNeoForgeModule extends HomesBackendModule {
	public HomesNeoForgeModule() {
		this.setup();
	}
}
