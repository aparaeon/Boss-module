package gg.mmorealms.module.login_rewards.backend.neoforge;

import gg.mmorealms.module.login_rewards.LoginRewardsModuleBuildConstants;
import gg.mmorealms.module.login_rewards.backend.common.LoginrewardsBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(LoginRewardsModuleBuildConstants.ID)
public class LoginrewardsNeoForgeModule extends LoginrewardsBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static LoginrewardsNeoForgeModule instance;

	public LoginrewardsNeoForgeModule() {
		LoginrewardsNeoForgeModule.instance = this;
		this.setup();
	}

}