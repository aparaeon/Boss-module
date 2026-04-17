package gg.mmorealms.module.kits.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;
import gg.mmorealms.module.kits.common.KitsCommonModule;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class KitsBackendModule extends KitsCommonModule implements BackendModule {
	@Getter
	@Accessors(fluent = true)
	private static KitsBackendModule instance;

	private @Inject BackendMiniMessageManager miniMessageManager;

	private @Inject FileManager fileManager;
	private @Inject MinecraftServer server;

	private KitsConfig config; // exported

	public KitsBackendModule() {
		KitsBackendModule.instance = this;
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(KitsConfig.class));
		KitUtils.register();
	}

	@Override
	public void onEnable() {
	}

}
