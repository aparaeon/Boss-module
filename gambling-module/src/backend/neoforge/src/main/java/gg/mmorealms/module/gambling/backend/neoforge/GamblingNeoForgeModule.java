package gg.mmorealms.module.gambling.backend.neoforge;

import gg.mmorealms.module.gambling.GamblingModuleBuildConstants;
import gg.mmorealms.module.gambling.backend.common.GamblingBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(GamblingModuleBuildConstants.ID)
public class GamblingNeoForgeModule extends GamblingBackendModule {

	public GamblingNeoForgeModule() {
		this.setup();
	}

}
