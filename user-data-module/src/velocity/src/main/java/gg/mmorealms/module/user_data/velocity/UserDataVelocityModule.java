package gg.mmorealms.module.user_data.velocity;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.user_data.UserDataModuleBuildConstants;
import gg.mmorealms.module.user_data.common.UserDataCommonModule;
import gg.mmorealms.module.user_data.velocity.manager.VelocityUserSettingsLoader;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = UserDataModuleBuildConstants.ID,
		name = UserDataModuleBuildConstants.ID,
		version = UserDataModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
@Getter
public class UserDataVelocityModule extends UserDataCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	public static UserDataVelocityModule instance;

	private @Inject ProxyServer server;

	private VelocityUserSettingsLoader userSettingsLoader;

	public UserDataVelocityModule() {
		UserDataVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.userSettingsLoader = new VelocityUserSettingsLoader();
	}

	@Override
	public void onEnable() {

	}
}
