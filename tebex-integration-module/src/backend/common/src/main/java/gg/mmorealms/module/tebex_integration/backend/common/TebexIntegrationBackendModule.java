package gg.mmorealms.module.tebex_integration.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.tebex_integration.backend.common.config.TebexIntegrationConfig;
import gg.mmorealms.module.tebex_integration.backend.common.manager.UserPurchaseRewardsDatabaseLoader;
import gg.mmorealms.module.tebex_integration.common.TebexIntegrationCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class TebexIntegrationBackendModule extends TebexIntegrationCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static TebexIntegrationBackendModule instance;

	private @Inject FileManager fileManager;

	private TebexIntegrationConfig config; // Exported
	private UserPurchaseRewardsDatabaseLoader userPurchaseRewardsDatabaseLoader;

	public TebexIntegrationBackendModule() {
		TebexIntegrationBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {
		this.config = export(fileManager.load(TebexIntegrationConfig.class));
		this.userPurchaseRewardsDatabaseLoader = new UserPurchaseRewardsDatabaseLoader();
	}

	@Override
	public void onEnable() throws ModuleException {

	}
}
