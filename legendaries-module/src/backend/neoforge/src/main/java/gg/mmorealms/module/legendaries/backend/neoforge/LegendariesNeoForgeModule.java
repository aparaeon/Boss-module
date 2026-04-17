package gg.mmorealms.module.legendaries.backend.neoforge;

import gg.mmorealms.module.legendaries.LegendariesModuleBuildConstants;
import gg.mmorealms.module.legendaries.backend.common.LegendariesBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(LegendariesModuleBuildConstants.ID)
public class LegendariesNeoForgeModule extends LegendariesBackendModule {
	public LegendariesNeoForgeModule() {
		this.setup();
	}
}
