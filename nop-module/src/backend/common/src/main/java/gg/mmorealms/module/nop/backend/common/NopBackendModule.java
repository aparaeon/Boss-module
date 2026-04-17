package gg.mmorealms.module.nop.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.nop.common.NopComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class NopBackendModule extends NopComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static NopBackendModule instance;

	public NopBackendModule() {
		NopBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}


}