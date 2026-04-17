package gg.mmorealms.module.core.backend.neoforge;

import gg.mmorealms.module.core.CoreModuleBuildConstants;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.neoforge.manager.NeoForgeGUIManager;
import net.neoforged.fml.common.Mod;

@Mod(CoreModuleBuildConstants.ID)
public class CoreNeoForgeModule extends CoreBackendModule {

	public CoreNeoForgeModule() {
		super(new NeoForgeGUIManager());
		this.setup();
	}

}
