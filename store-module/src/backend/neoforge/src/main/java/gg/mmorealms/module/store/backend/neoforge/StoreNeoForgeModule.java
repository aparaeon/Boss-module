package gg.mmorealms.module.store.backend.neoforge;

import gg.mmorealms.module.store.StoreModuleBuildConstants;
import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(StoreModuleBuildConstants.ID)
public class StoreNeoForgeModule extends StoreBackendModule {

	public StoreNeoForgeModule() {
		this.setup();
	}

}
