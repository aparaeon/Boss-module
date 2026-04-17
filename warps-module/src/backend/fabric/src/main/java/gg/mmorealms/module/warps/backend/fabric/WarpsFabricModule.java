package gg.mmorealms.module.warps.backend.fabric;

import gg.mmorealms.module.warps.backend.common.WarpsBackendModule;
import net.fabricmc.api.ModInitializer;

public class WarpsFabricModule extends WarpsBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
