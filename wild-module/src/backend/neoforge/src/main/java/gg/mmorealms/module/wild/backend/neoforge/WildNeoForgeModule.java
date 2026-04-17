package gg.mmorealms.module.wild.backend.neoforge;

import gg.mmorealms.module.wild.WildModuleBuildConstants;
import gg.mmorealms.module.wild.backend.common.WildBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(WildModuleBuildConstants.ID)
public class WildNeoForgeModule extends WildBackendModule {
	public WildNeoForgeModule() {
		this.setup();
	}
}
