package gg.mmorealms.module.crates.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.crates.backend.common.config.CratesConfig;
import gg.mmorealms.module.crates.backend.common.manager.CrateKeysLoader;
import gg.mmorealms.module.crates.common.CratesCommonModule;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public class CratesBackendModule extends CratesCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static CratesBackendModule instance;

	private @Inject FileManager fileManager;
	private @Inject MinecraftServer server;

	private CratesConfig config; // exported

	private CrateKeysLoader crateKeysLoader;

	public CratesBackendModule() {
		CratesBackendModule.instance = this;
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(CratesConfig.class));
		this.crateKeysLoader = new CrateKeysLoader();
	}

	@Override
	public void onEnable() {

	}
}
