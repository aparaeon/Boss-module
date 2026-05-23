package gg.mmorealms.module.login_rewards.backend.fabric;

import gg.mmorealms.module.login_rewards.backend.common.LoginrewardsBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

public class LoginrewardsFabricModule extends LoginrewardsBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static LoginrewardsFabricModule instance;

	public LoginrewardsFabricModule() {
		LoginrewardsFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}