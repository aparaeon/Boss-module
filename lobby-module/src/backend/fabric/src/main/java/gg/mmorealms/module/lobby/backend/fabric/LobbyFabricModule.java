package gg.mmorealms.module.lobby.backend.fabric;

import gg.mmorealms.module.lobby.backend.common.LobbyBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

public class LobbyFabricModule extends LobbyBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static LobbyFabricModule instance;

	public LobbyFabricModule() {
		LobbyFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}