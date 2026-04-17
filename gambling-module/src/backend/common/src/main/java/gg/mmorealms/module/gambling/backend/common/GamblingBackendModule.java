package gg.mmorealms.module.gambling.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.chat.backend.common.manager.BackendChatInputManager;
import gg.mmorealms.module.gambling.backend.common.files.GambleConfig;
import gg.mmorealms.module.gambling.common.GamblingCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public class GamblingBackendModule extends GamblingCommonModule implements BackendModule {

	// Static
	@Getter
	@Accessors(fluent = true)
	private static GamblingBackendModule instance;

	private @Inject BackendMiniMessageManager miniMessageManager;
	private @Inject MinecraftServer server;
	private @Inject FileManager fileManager;
	private @Inject BackendChatInputManager chatInputManager;

	private GambleConfig config;

	public GamblingBackendModule() {
		instance = this;
	}

	@Override
	public void onInit() {
		this.config = fileManager.load(GambleConfig.class);
	}

	@Override
	public void onEnable() {
	}
}