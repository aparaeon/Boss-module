package gg.mmorealms.module.realms.backend.neoforge;

import gg.mmorealms.module.realms.RealmsModuleBuildConstants;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(RealmsModuleBuildConstants.ID)
public class RealmsNeoForgeModule extends RealmsBackendModule {

	public RealmsNeoForgeModule() {
		this.setup();
	}

}
