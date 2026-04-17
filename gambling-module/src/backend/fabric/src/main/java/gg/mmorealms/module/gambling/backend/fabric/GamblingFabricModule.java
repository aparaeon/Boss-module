package gg.mmorealms.module.gambling.backend.fabric;

import gg.mmorealms.module.gambling.backend.common.GamblingBackendModule;
import net.fabricmc.api.ModInitializer;

public class GamblingFabricModule extends GamblingBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}

}
