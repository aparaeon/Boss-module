package gg.mmorealms.module.scoreboard.backend.fabric;

import gg.mmorealms.module.scoreboard.backend.common.ScoreboardBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

public class ScoreboardFabricModule extends ScoreboardBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static ScoreboardFabricModule instance;

	public ScoreboardFabricModule() {
		ScoreboardFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}