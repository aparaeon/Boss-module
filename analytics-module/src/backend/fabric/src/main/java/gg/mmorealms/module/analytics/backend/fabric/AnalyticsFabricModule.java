package gg.mmorealms.module.analytics.backend.fabric;

import gg.mmorealms.module.analytics.backend.common.AnalyticsBackendModule;
import net.fabricmc.api.ModInitializer;

public class AnalyticsFabricModule extends AnalyticsBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}
}
