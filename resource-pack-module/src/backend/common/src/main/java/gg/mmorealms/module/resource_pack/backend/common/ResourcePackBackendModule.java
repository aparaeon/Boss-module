package gg.mmorealms.module.resource_pack.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.resource_pack.common.ResourcePackComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class ResourcePackBackendModule extends ResourcePackComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static ResourcePackBackendModule instance;

	public ResourcePackBackendModule() {
		ResourcePackBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}


}