package gg.mmorealms.module.nop.backend.neoforge;

import gg.mmorealms.module.nop.NopModuleBuildConstants;
import gg.mmorealms.module.nop.backend.common.NopBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(NopModuleBuildConstants.ID)
public class NopNeoForgeModule extends NopBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static NopNeoForgeModule instance;

	public NopNeoForgeModule() {
		NopNeoForgeModule.instance = this;
	}

}