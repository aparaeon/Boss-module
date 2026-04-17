package gg.mmorealms.module.analytics.backend.neoforge;

import gg.mmorealms.module.analytics.AnalyticsModuleBuildConstants;
import gg.mmorealms.module.analytics.backend.common.AnalyticsBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(AnalyticsModuleBuildConstants.ID)
public class AnalyticsNeoForgeModule extends AnalyticsBackendModule {

	public AnalyticsNeoForgeModule() {
		this.setup();
	}

}
