package gg.mmorealms.module.realms.velocity;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.core.common.command.IDumpCommand;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.realms.RealmsModuleBuildConstants;
import gg.mmorealms.module.realms.common.RealmsCommonModule;
import gg.mmorealms.module.realms.velocity.config.RealmsConfig;
import gg.mmorealms.module.realms.velocity.manager.RealmsManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = RealmsModuleBuildConstants.ID,
		name = RealmsModuleBuildConstants.ID,
		version = RealmsModuleBuildConstants.VERSION,
		authors = {"Radu Voinea", "Andrei-Madalin Coman"}
)
public class RealmsVelocityModule extends RealmsCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static RealmsVelocityModule instance;

	private @Inject ServerManager serverManager;
	private @Inject ProxyServer proxyServer;
	private @Inject FileManager fileManager;

	private RealmsConfig config; // exported

	private RealmsManager realmsManager; // exported

	public RealmsVelocityModule() {
		RealmsVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(RealmsConfig.class));

		this.realmsManager = export(new RealmsManager());

		IDumpCommand.registerAdditionalDump(this.getModuleAnnotation(), "Realms State", () -> realmsManager.dump());
	}

	@Override
	public void onEnable() {

	}

}
