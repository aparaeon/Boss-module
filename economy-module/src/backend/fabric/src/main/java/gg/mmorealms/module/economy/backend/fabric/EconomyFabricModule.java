package gg.mmorealms.module.economy.backend.fabric;

import gg.mmorealms.module.economy.backend.common.EconomyBackendModule;
import net.fabricmc.api.ModInitializer;

public class EconomyFabricModule extends EconomyBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
