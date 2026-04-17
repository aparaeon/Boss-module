package gg.mmorealms.module.tutorial.backend.neoforge;

import gg.mmorealms.module.tutorial.TutorialModuleBuildConstants;
import gg.mmorealms.module.tutorial.backend.common.TutorialBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(TutorialModuleBuildConstants.ID)
public class TutorialNeoForgeModule extends TutorialBackendModule {

	public TutorialNeoForgeModule() {
		this.setup();
	}

}
