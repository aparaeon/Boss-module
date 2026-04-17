package gg.mmorealms.module.tutorial.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.tutorial.common.TutorialCoreModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class TutorialBackendModule extends TutorialCoreModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static TutorialBackendModule instance;

	public TutorialBackendModule(){
		TutorialBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}
