package gg.mmorealms.module.core.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.core.CoreModuleBuildConstants;
import gg.mmorealms.module.core.common.CoreCommonModule;
import gg.mmorealms.module.core.common.dto.server_location.ServerTypeLocation;
import gg.mmorealms.module.core.velocity.config.CoreConfig;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.core.velocity.manager.VelocityCooldownsLoader;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = CoreModuleBuildConstants.ID,
		name = CoreModuleBuildConstants.ID,
		version = CoreModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class CoreVelocityModule extends CoreCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static CoreVelocityModule instance;

	private @Inject ProxyServer proxy;
	private @Inject FileManager fileManager;
	private @Inject VelocityMiniMessageManager miniMessageManager;

	private ServerManager serverManager; // exported
	private VelocityCooldownsLoader velocityCooldownsLoader;

	private CoreConfig config; // exported

	public CoreVelocityModule() {
		CoreVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(CoreConfig.class));
		this.init(config);

		this.velocityCooldownsLoader = new VelocityCooldownsLoader();

		this.serverManager = export(new ServerManager());
		ServerTypeLocation.setFetchServerType((ServerType serverType) -> {
			EngineServer server = serverManager.getLowestUsageServer(serverType);

			if (server == null) {
				return null;
			}

			return server.getServerID();
		});

	}

	@Override
	public void onEnable() {
	}
}
