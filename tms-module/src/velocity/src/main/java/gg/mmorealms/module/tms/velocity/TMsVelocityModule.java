package gg.mmorealms.module.tms.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.tms.TmsModuleBuildConstants;
import gg.mmorealms.module.tms.common.TMsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter

@Plugin(
		id = TmsModuleBuildConstants.ID,
		name = TmsModuleBuildConstants.ID,
		version = TmsModuleBuildConstants.VERSION,
		authors = {"ZeroDelusions"},
		dependencies = {}
)
public class TMsVelocityModule extends TMsCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static TMsVelocityModule instance;

	public TMsVelocityModule() {
		TMsVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}
