package gg.mmorealms.module.warps.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.warps.backend.common.config.WarpsConfig;
import gg.mmorealms.module.warps.common.WarpsCommonModule;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class WarpsBackendModule extends WarpsCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static WarpsBackendModule instance;

	private WarpsConfig config;

	private @Inject FileManager fileManager;
	private @Inject MinecraftServer server;

	public WarpsBackendModule() {
		WarpsBackendModule.instance = this;
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(WarpsConfig.class));
	}

	@Override
	public void onEnable() {

	}
}
