package gg.mmorealms.module.tutorial.backend.fabric;

import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.tutorial.backend.common.TutorialBackendModule;
import net.fabricmc.api.ModInitializer;

public class TutorialFabricModule extends TutorialBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}

	@Override
	public void onEnable() throws ModuleException {

	}
}
