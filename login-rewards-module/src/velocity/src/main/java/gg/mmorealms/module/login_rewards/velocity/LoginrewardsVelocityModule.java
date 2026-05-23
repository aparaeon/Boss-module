package gg.mmorealms.module.login_rewards.velocity;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.login_rewards.LoginRewardsModuleBuildConstants;
import gg.mmorealms.module.login_rewards.common.LoginrewardsCommonModule;
import gg.mmorealms.module.login_rewards.velocity.config.LoginRewardsModuleConfig;
import gg.mmorealms.module.login_rewards.velocity.manager.DailyManager;
import gg.mmorealms.module.login_rewards.velocity.manager.loader.UserDailyLoader;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
	id = LoginRewardsModuleBuildConstants.ID,
	name = LoginRewardsModuleBuildConstants.ID,
	version = LoginRewardsModuleBuildConstants.VERSION,
	authors = {"Kaioshiyazaki"}
)
@Getter
public class LoginrewardsVelocityModule extends LoginrewardsCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static LoginrewardsVelocityModule instance;

	private @Inject ProxyServer proxy;
	private @Inject FileManager fileManager;

	private LoginRewardsModuleConfig config;
	private UserDailyLoader userDailyLoader;
	private DailyManager dailyManager;

	public LoginrewardsVelocityModule() {
		LoginrewardsVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {
		this.config = export(fileManager.load(LoginRewardsModuleConfig.class));
		this.userDailyLoader = export(new UserDailyLoader());
		this.dailyManager = export(new DailyManager());
	}

	@Override
	public void onEnable() throws ModuleException {

	}
}
