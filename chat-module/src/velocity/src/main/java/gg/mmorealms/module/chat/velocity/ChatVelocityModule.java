package gg.mmorealms.module.chat.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.chat.ChatModuleBuildConstants;
import gg.mmorealms.module.chat.common.ChatCommonModule;
import gg.mmorealms.module.chat.velocity.config.ChatConfig;
import gg.mmorealms.module.chat.velocity.config.StaffChatConfig;
import gg.mmorealms.module.chat.velocity.manager.ChatManager;
import gg.mmorealms.module.chat.velocity.manager.MessageManager;
import gg.mmorealms.module.chat.velocity.manager.VelocityChatInputManager;
import gg.mmorealms.module.chat.velocity.manager.staff_chat.StaffChatManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = ChatModuleBuildConstants.ID,
		name = ChatModuleBuildConstants.ID,
		version = ChatModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class ChatVelocityModule extends ChatCommonModule implements VelocityModule {

	public static final String CHAT_MUTE_OVERRIDE_PERMISSION = "mmorealms.chat.mute.override";
	public static final String STAFF_CHAT_PERMISSION = "mmorealms.chat.staff";

	@Getter
	@Accessors(fluent = true)
	private static ChatVelocityModule instance;

	private @Inject ProxyServer proxy;
	private @Inject FileManager fileManager;
	private @Inject VelocityMiniMessageManager miniMessageManager;

	private MessageManager messageManager;
	private ChatConfig config;                          // Exported
	private StaffChatConfig staffChatConfig;            // Exported
	private VelocityChatInputManager chatInputManager;  // exported
	private ChatManager chatManager;                    // Exported
	private StaffChatManager staffChatManager;          // Exported

	public ChatVelocityModule() {
		ChatVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(ChatConfig.class));
		this.staffChatConfig = export(fileManager.load(StaffChatConfig.class));

		this.messageManager = new MessageManager();
		this.chatInputManager = export(new VelocityChatInputManager());
		this.chatManager = export(new ChatManager());
		this.staffChatManager = export(new StaffChatManager());
	}

	@Override
	public void onEnable() {
	}
}
