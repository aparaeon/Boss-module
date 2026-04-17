package gg.mmorealms.module.homes.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.homes.HomesModuleBuildConstants;
import gg.mmorealms.module.homes.common.HomesCommonModule;
import gg.mmorealms.module.homes.velocity.manager.HomesVelocityUtils;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = HomesModuleBuildConstants.ID,
		name = HomesModuleBuildConstants.ID,
		version = HomesModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman"}
)
public class HomesModule extends HomesCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static HomesModule instance;

	public HomesModule() {
		HomesModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
		HomesVelocityUtils.deleteWildHomes();
	}

}
