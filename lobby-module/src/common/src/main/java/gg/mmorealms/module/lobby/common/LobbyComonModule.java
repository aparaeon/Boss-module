package gg.mmorealms.module.lobby.common;

import gg.mmorealms.loader.common.dto.CommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class LobbyComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static LobbyComonModule instance;

	public LobbyComonModule() {
		LobbyComonModule.instance = this;
	}
}