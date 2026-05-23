package gg.mmorealms.module.login_rewards.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.login_rewards.backend.common.config.LoginRewardsBackendConfig;
import gg.mmorealms.module.login_rewards.common.LoginrewardsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class LoginrewardsBackendModule extends LoginrewardsCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static LoginrewardsBackendModule instance;

	public LoginrewardsBackendModule() {
		LoginrewardsBackendModule.instance = this;
	}

	private @Inject FileManager fileManager;
	private LoginRewardsBackendConfig config;

	@Override
	public void onInit() throws ModuleException {
		this.config = export(fileManager.load(LoginRewardsBackendConfig.class));
	}

	@Override
	public void onEnable() throws ModuleException {

	}

}
