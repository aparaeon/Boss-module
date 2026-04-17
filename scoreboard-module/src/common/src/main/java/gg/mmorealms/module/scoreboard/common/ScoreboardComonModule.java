package gg.mmorealms.module.scoreboard.common;

import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.scoreboard.ScoreboardModuleBuildConstants;
import gg.mmorealms.loader.common.annotation.Module;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Module(
	id = ScoreboardModuleBuildConstants.ID,
	version = ScoreboardModuleBuildConstants.VERSION,
	authors = {"Radu Voinea"},
	dependencies = ScoreboardModuleBuildConstants.DEPENDENCIES
)
public abstract class ScoreboardComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static ScoreboardComonModule instance;

	public ScoreboardComonModule() {
		ScoreboardComonModule.instance = this;
	}
}