package gg.mmorealms.module.hunts.backend.neoforge;

import gg.mmorealms.module.hunts.HuntsModuleBuildConstants;
import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(HuntsModuleBuildConstants.ID)
public class HuntsNeoForgeModule extends HuntsBackendModule {
	public HuntsNeoForgeModule() {
		this.setup();
	}
}
