package gg.mmorealms.module.resource_pack.backend.fabric;

import gg.mmorealms.module.resource_pack.backend.common.ResourcePackBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

public class ResourcePackFabricModule extends ResourcePackBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static ResourcePackFabricModule instance;

	public ResourcePackFabricModule() {
		ResourcePackFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}