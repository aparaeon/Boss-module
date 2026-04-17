package gg.mmorealms.module.core_protect.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.core_protect.common.CoreProtectComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class CoreProtectBackendModule extends CoreProtectComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static CoreProtectBackendModule instance;

	public CoreProtectBackendModule() {
		CoreProtectBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}


}