package gg.mmorealms.module.store.backend.fabric;

import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import net.fabricmc.api.ModInitializer;

public class StoreFabricModule extends StoreBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}
}
