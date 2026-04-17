package gg.mmorealms.module.resource_pack.backend.neoforge;

import gg.mmorealms.module.resource_pack.ResourcePackModuleBuildConstants;
import gg.mmorealms.module.resource_pack.backend.common.ResourcePackBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(ResourcePackModuleBuildConstants.ID)
public class ResourcePackNeoForgeModule extends ResourcePackBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static ResourcePackNeoForgeModule instance;

	public ResourcePackNeoForgeModule() {
		ResourcePackNeoForgeModule.instance = this;
	}

}