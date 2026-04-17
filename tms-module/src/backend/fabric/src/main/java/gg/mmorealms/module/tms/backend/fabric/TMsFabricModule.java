package gg.mmorealms.module.tms.backend.fabric;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.module.tms.backend.common.TMsBackendModule;
import gg.mmorealms.module.tms.backend.fabric.config.TMsConfig;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

@Getter
public class TMsFabricModule extends TMsBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	private static TMsFabricModule instance;

	private @Inject RegistryAccess registryAccess;
	private @Inject MinecraftServer server;
	private @Inject FileManager fileManager;

	private TMsConfig config; // exported

	public TMsFabricModule() {
		TMsFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(TMsConfig.class));
		this.config.initSortedTmMoves();
	}

	@Override
	public void onEnable() {

	}

}