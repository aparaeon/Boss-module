package gg.mmorealms.module.limbo.backend.fabric;

import gg.mmorealms.module.limbo.backend.common.LimboBackendModule;
import net.fabricmc.api.ModInitializer;

public class LimboFabricModule extends LimboBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}
}
