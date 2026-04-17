package gg.mmorealms.module.tms.backend.neoforge;

import gg.mmorealms.module.tms.TmsModuleBuildConstants;
import gg.mmorealms.module.tms.backend.common.TMsBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(TmsModuleBuildConstants.ID)
public class TMsNeoForgeModule extends TMsBackendModule {
	public TMsNeoForgeModule() {
		this.setup();
	}
}
