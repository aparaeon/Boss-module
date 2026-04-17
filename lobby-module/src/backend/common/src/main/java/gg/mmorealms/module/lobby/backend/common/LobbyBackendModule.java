package gg.mmorealms.module.lobby.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.lobby.common.LobbyComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class LobbyBackendModule extends LobbyComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static LobbyBackendModule instance;

	public LobbyBackendModule() {
		LobbyBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}


}