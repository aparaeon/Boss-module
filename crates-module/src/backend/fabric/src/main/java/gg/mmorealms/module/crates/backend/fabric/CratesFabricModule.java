package gg.mmorealms.module.crates.backend.fabric;

import gg.mmorealms.module.crates.backend.common.CratesBackendModule;
import net.fabricmc.api.ModInitializer;

public class CratesFabricModule extends CratesBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
