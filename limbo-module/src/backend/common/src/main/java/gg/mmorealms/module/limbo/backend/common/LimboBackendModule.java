package gg.mmorealms.module.limbo.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.limbo.common.LimboCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class LimboBackendModule extends LimboCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static LimboBackendModule instance;

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}
