package gg.mmorealms.module.plushies.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.plushies.PlushiesModuleBuildConstants;
import gg.mmorealms.module.plushies.common.PlushiesCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter

@Plugin(
		id = PlushiesModuleBuildConstants.ID,
		name = PlushiesModuleBuildConstants.ID,
		version = PlushiesModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class PlushiesVelocityModule extends PlushiesCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static PlushiesVelocityModule instance;

	public PlushiesVelocityModule() {
		PlushiesVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}
