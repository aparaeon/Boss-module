package gg.mmorealms.module.catch_combo.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.catch_combo.common.CatchComboComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class CatchComboBackendModule extends CatchComboComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static CatchComboBackendModule instance;

	public CatchComboBackendModule() {
		CatchComboBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}


}