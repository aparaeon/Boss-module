package gg.mmorealms.module.lobby.backend.neoforge;

import gg.mmorealms.module.lobby.LobbyModuleBuildConstants;
import gg.mmorealms.module.lobby.backend.common.LobbyBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(LobbyModuleBuildConstants.ID)
public class LobbyNeoForgeModule extends LobbyBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static LobbyNeoForgeModule instance;

	public LobbyNeoForgeModule() {
		LobbyNeoForgeModule.instance = this;
	}

}