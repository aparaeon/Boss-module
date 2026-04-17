package gg.mmorealms.module.plushies.backend.fabric;

import gg.mmorealms.module.plushies.backend.common.PlushiesBackendModule;
import gg.mmorealms.module.plushies.backend.common.manager.IPlushiesPlatformImplementation;
import gg.mmorealms.module.plushies.backend.fabric.manager.PlushiesFabricPlatformImplementation;
import net.fabricmc.api.ModInitializer;

public class PlushiesFabricModule extends PlushiesBackendModule implements ModInitializer {

	@Override
	protected IPlushiesPlatformImplementation createPlatformImplementation() {
		return new PlushiesFabricPlatformImplementation();
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}
