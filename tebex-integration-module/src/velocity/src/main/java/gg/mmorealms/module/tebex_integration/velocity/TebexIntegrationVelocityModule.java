package gg.mmorealms.module.tebex_integration.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.tebex_integration.TebexIntegrationModuleBuildConstants;
import gg.mmorealms.module.tebex_integration.common.TebexIntegrationCommonModule;
import gg.mmorealms.module.tebex_integration.velocity.config.TebexIntegrationConfig;
import gg.mmorealms.module.tebex_integration.velocity.manager.CouponManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = TebexIntegrationModuleBuildConstants.ID,
		name = TebexIntegrationModuleBuildConstants.ID,
		version = TebexIntegrationModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class TebexIntegrationVelocityModule extends TebexIntegrationCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static TebexIntegrationVelocityModule instance;

	private @Inject ProxyServer proxy;
	private @Inject FileManager fileManager;
	private @Inject VelocityMiniMessageManager miniMessageManager;

	private CouponManager couponManager;
	private TebexIntegrationConfig config;

	public TebexIntegrationVelocityModule() {
		TebexIntegrationVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(this.fileManager.load(TebexIntegrationConfig.class));
		this.couponManager = export(new CouponManager());
	}

	@Override
	public void onEnable() {

	}
}
