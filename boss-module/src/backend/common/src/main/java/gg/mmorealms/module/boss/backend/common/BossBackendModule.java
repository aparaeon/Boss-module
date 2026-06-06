package gg.mmorealms.module.boss.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.boss.common.BossComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class BossBackendModule extends BossComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static BossBackendModule instance;

	public BossBackendModule() {
		BossBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}


}