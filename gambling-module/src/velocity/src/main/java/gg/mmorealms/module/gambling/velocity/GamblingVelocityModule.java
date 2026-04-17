package gg.mmorealms.module.gambling.velocity;

import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.gambling.GamblingModuleBuildConstants;
import gg.mmorealms.module.gambling.common.GamblingCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = GamblingModuleBuildConstants.ID,
		name = GamblingModuleBuildConstants.ID,
		version = GamblingModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class GamblingVelocityModule extends GamblingCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static GamblingVelocityModule instance;

	public GamblingVelocityModule() {
		GamblingVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}
