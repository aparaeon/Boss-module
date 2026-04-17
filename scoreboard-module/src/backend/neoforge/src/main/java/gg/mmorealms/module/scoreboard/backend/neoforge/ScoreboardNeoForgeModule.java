package gg.mmorealms.module.scoreboard.backend.neoforge;

import gg.mmorealms.module.scoreboard.ScoreboardModuleBuildConstants;
import gg.mmorealms.module.scoreboard.backend.common.ScoreboardBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(ScoreboardModuleBuildConstants.ID)
public class ScoreboardNeoForgeModule extends ScoreboardBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static ScoreboardNeoForgeModule instance;

	public ScoreboardNeoForgeModule() {
		ScoreboardNeoForgeModule.instance = this;
	}

}