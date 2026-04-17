package gg.mmorealms.module.kits.backend.fabric;

import gg.mmorealms.module.kits.backend.common.KitsBackendModule;
import net.fabricmc.api.ModInitializer;

public class KitsFabricModule extends KitsBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
