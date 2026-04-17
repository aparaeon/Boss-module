package gg.mmorealms.module.gyms.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.gyms.GymsModuleBuildConstants;
import gg.mmorealms.module.gyms.common.GymsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = GymsModuleBuildConstants.ID,
		name = GymsModuleBuildConstants.ID,
		version = GymsModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman"}
)
public class GymsVelocityModule extends GymsCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static GymsVelocityModule instance;

	public GymsVelocityModule() {
		GymsVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}