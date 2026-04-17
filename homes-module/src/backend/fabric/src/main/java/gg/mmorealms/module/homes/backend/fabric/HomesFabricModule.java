package gg.mmorealms.module.homes.backend.fabric;

import gg.mmorealms.module.homes.backend.common.HomesBackendModule;
import net.fabricmc.api.ModInitializer;

public class HomesFabricModule extends HomesBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
