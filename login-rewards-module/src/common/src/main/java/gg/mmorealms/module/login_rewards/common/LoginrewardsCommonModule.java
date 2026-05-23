package gg.mmorealms.module.login_rewards.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.login_rewards.LoginRewardsModuleBuildConstants;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Module(
	id = LoginRewardsModuleBuildConstants.ID,
	version = LoginRewardsModuleBuildConstants.VERSION,
	authors = {"Kaioshiyazaki"},
	dependencies = LoginRewardsModuleBuildConstants.DEPENDENCIES
)
public abstract class LoginrewardsCommonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static LoginrewardsCommonModule instance;

	public LoginrewardsCommonModule() {
		LoginrewardsCommonModule.instance = this;
	}
}
