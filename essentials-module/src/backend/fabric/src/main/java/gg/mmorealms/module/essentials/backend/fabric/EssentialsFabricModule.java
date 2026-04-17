package gg.mmorealms.module.essentials.backend.fabric;

import gg.mmorealms.module.essentials.backend.common.EssentialsBackendModule;
import net.fabricmc.api.ModInitializer;

public class EssentialsFabricModule extends EssentialsBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}

}
