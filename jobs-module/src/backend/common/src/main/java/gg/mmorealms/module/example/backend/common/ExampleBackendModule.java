package gg.mmorealms.module.example.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.example.common.ExampleComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class ExampleBackendModule extends ExampleComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static ExampleBackendModule instance;

	public ExampleBackendModule() {
		ExampleBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}


}