package gg.mmorealms.module.realms.backend.fabric;

import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import net.fabricmc.api.ModInitializer;

public class RealmsFabricModule extends RealmsBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
