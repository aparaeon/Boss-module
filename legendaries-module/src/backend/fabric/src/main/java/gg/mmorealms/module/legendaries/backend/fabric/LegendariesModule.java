package gg.mmorealms.module.legendaries.backend.fabric;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.legendaries.backend.common.LegendariesBackendModule;
import gg.mmorealms.module.legendaries.backend.fabric.config.LegendarySpawnConfig;
import gg.mmorealms.module.legendaries.backend.fabric.manager.LegendaryDespawnManager;
import gg.mmorealms.module.legendaries.backend.fabric.manager.LegendaryInfoManager;
import gg.mmorealms.module.legendaries.backend.fabric.manager.LegendarySpawnManager;
import gg.mmorealms.module.legendaries.backend.fabric.manager.listener.CobblemonEventListener;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;

@Getter
public class LegendariesModule extends LegendariesBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	private static LegendariesModule instance;

	private @Inject MinecraftServer server;
	private @Inject FileManager fileManager;

	private LegendarySpawnConfig config; // exported
	private LegendaryInfoManager infoManager;

	private LegendarySpawnManager spawnManager;
	private LegendaryDespawnManager despawnManager;

	private CobblemonEventListener eventListener;

	private ServerType serverType;

	public LegendariesModule() {
		LegendariesModule.instance = this;
	}

	@Override
	public void onInit() {
		this.serverType = BackendLoader.instance().getServerType();
		this.config = export(fileManager.load(LegendarySpawnConfig.class));

		this.infoManager = new LegendaryInfoManager();

		if (serverType == ServerType.WILD) {
			this.spawnManager = new LegendarySpawnManager(this.infoManager);
			this.despawnManager = new LegendaryDespawnManager(this.infoManager);
			this.eventListener = new CobblemonEventListener(this.infoManager, this.despawnManager);
		}
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}
