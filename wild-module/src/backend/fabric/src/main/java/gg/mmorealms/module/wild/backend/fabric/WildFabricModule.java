package gg.mmorealms.module.wild.backend.fabric;

import gg.mmorealms.module.wild.backend.common.WildBackendModule;
import net.fabricmc.api.ModInitializer;

public class WildFabricModule extends WildBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
