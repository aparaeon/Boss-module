package gg.mmorealms.module.core.backend.fabric;

import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.fabric.manager.FabricGUIManager;
import net.fabricmc.api.ModInitializer;

public class CoreFabricModule extends CoreBackendModule implements ModInitializer {

	public CoreFabricModule() {
		super(new FabricGUIManager());
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}
