package gg.mmorealms.module.lobby.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.lobby.LobbyBuildConstants;
import gg.mmorealms.module.lobby.common.LobbyComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = LobbyBuildConstants.ID,
		name = LobbyBuildConstants.ID,
		version = LobbyBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
@Getter
public class LobbyVelocityModule extends LobbyComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static LobbyVelocityModule instance;

	public LobbyVelocityModule() {
		LobbyVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}