package gg.mmorealms.module.tebex_integration.backend.fabric;

import gg.mmorealms.module.tebex_integration.backend.common.TebexIntegrationBackendModule;
import net.fabricmc.api.ModInitializer;

public class TebexIntegrationFabricModule extends TebexIntegrationBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}
}
