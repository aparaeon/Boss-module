package gg.mmorealms.module.essentials.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityCommandManager;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.core.common.dto.NetworkLocation;
import gg.mmorealms.module.essentials.EssentialsModuleBuildConstants;
import gg.mmorealms.module.essentials.common.EssentialsCommonModule;
import gg.mmorealms.module.essentials.velocity.config.EssentialsConfig;
import gg.mmorealms.module.essentials.velocity.config.SimpleCommandsConfig;
import gg.mmorealms.module.essentials.velocity.manager.AlertManager;
import gg.mmorealms.module.essentials.velocity.manager.CommandRegistrar;
import gg.mmorealms.module.essentials.velocity.manager.TeleportManager;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.UUID;

@Getter

@Plugin(
		id = EssentialsModuleBuildConstants.ID,
		name = EssentialsModuleBuildConstants.ID,
		version = EssentialsModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman", "Radu Voinea"}
)
public class EssentialsVelocityModule extends EssentialsCommonModule implements VelocityModule {

	@Getter
	private static final HashMap<UUID, NetworkLocation> lastLocations = new HashMap<>();

	@Getter
	@Accessors(fluent = true)
	private static EssentialsVelocityModule instance;

	private @Inject FileManager fileManager;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject ProxyServer proxy;

	private @Inject VelocityCommandManager commandManager;

	private TeleportManager teleportManager;
	private AlertManager alertManager;

	private EssentialsConfig config; // exported

	public EssentialsVelocityModule() {
		EssentialsVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(EssentialsConfig.class));
		this.teleportManager = new TeleportManager();
		this.alertManager = new AlertManager();
		SimpleCommandsConfig.init(fileManager);
	}

	@Override
	public void onEnable() {
		CommandRegistrar.register();
	}
}
