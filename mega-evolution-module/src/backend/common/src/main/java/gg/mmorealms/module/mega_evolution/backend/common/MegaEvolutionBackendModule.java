package gg.mmorealms.module.mega_evolution.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.mega_evolution.common.MegaEvolutionComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class MegaEvolutionBackendModule extends MegaEvolutionComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static MegaEvolutionBackendModule instance;

	public MegaEvolutionBackendModule() {
		MegaEvolutionBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}

}