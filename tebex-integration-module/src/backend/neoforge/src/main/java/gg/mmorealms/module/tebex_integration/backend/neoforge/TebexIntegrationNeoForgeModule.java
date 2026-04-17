package gg.mmorealms.module.tebex_integration.backend.neoforge;

import gg.mmorealms.module.tebex_integration.TebexIntegrationModuleBuildConstants;
import gg.mmorealms.module.tebex_integration.backend.common.TebexIntegrationBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(TebexIntegrationModuleBuildConstants.ID)
public class TebexIntegrationNeoForgeModule extends TebexIntegrationBackendModule {

	public TebexIntegrationNeoForgeModule() {
		this.setup();
	}

}
