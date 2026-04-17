package gg.mmorealms.module.core_protect.backend.neoforge;

import gg.mmorealms.module.core_protect.CoreProtectModuleBuildConstants;
import gg.mmorealms.module.core_protect.backend.common.CoreProtectBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(CoreProtectModuleBuildConstants.ID)
public class CoreProtectNeoForgeModule extends CoreProtectBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static CoreProtectNeoForgeModule instance;

	public CoreProtectNeoForgeModule() {
		CoreProtectNeoForgeModule.instance = this;
	}

}