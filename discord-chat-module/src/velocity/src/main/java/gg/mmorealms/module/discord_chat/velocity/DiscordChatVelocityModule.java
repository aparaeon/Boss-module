package gg.mmorealms.module.discord_chat.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.discord_chat.DiscordChatModuleBuildConstants;
import gg.mmorealms.module.discord_chat.common.DiscordChatCommonModule;
import gg.mmorealms.module.discord_chat.velocity.config.DiscordChatConfig;
import gg.mmorealms.module.discord_chat.velocity.dto.DiscordChatBot;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = DiscordChatModuleBuildConstants.ID,
		name = DiscordChatModuleBuildConstants.ID,
		version = DiscordChatModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = {
		}
)
public class DiscordChatVelocityModule extends DiscordChatCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static DiscordChatVelocityModule instance;

	private @Inject ProxyServer proxy;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject FileManager fileManager;

	private DiscordChatBot discordChatBot;

	private DiscordChatConfig config; // exported

	public DiscordChatVelocityModule() {
		DiscordChatVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {
		this.config = export(fileManager.load(DiscordChatConfig.class));

		this.discordChatBot = export(new DiscordChatBot(SecretsUtils.getEnvironmentVariable("JDA_CHAT_TOKEN")));
	}

	@Override
	public void onEnable() {

	}

}
