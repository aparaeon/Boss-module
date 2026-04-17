package gg.mmorealms.module.wild.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.wild.WildModuleBuildConstants;
import gg.mmorealms.module.wild.common.WildCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = WildModuleBuildConstants.ID,
		name = WildModuleBuildConstants.ID,
		version = WildModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class WildVelocityModule extends WildCommonModule implements VelocityModule {

	@Accessors(fluent = true)
	private static WildVelocityModule instance;

	public WildVelocityModule() {
		WildVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}
