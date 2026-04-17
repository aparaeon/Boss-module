package gg.mmorealms.module.scoreboard.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.scoreboard.ScoreboardModuleBuildConstants;
import gg.mmorealms.module.scoreboard.common.ScoreboardComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = ScoreboardModuleBuildConstants.ID,
		name = ScoreboardModuleBuildConstants.ID,
		version = ScoreboardModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
@Getter
public class ScoreboardVelocityModule extends ScoreboardComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static ScoreboardVelocityModule instance;

	public ScoreboardVelocityModule() {
		ScoreboardVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}