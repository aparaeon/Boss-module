package gg.mmorealms.module.chat.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.chat.backend.common.files.ChatConfig;
import gg.mmorealms.module.chat.backend.common.manager.BackendChatInputManager;
import gg.mmorealms.module.chat.common.ChatCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public class ChatBackendModule extends ChatCommonModule implements BackendModule {

	// Static
	@Getter
	@Accessors(fluent = true)
	private static ChatBackendModule instance;

	private @Inject BackendMiniMessageManager miniMessageManager;
	private @Inject MinecraftServer server;
	private @Inject FileManager fileManager;

	private BackendChatInputManager chatInputManager; // Exported

	private ChatConfig config;

	public ChatBackendModule() {
		instance = this;
	}

	@Override
	public void onInit() {
		this.config = this.fileManager.load(ChatConfig.class);
		this.chatInputManager = export(new BackendChatInputManager());
	}

	@Override
	public void onEnable() {
	}
}