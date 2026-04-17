package gg.mmorealms.module.discord_link.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.discord_link.DiscordLinkModuleBuildConstants;
import gg.mmorealms.module.discord_link.common.DiscordLinkCommonModule;
import gg.mmorealms.module.discord_link.velocity.config.DiscordLinkConfig;
import gg.mmorealms.module.discord_link.velocity.dto.DiscordLinkingBot;
import gg.mmorealms.module.discord_link.velocity.manager.DiscordLinkedUserDatabaseLoader;
import gg.mmorealms.module.discord_link.velocity.manager.DiscordLinkerManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter

@Plugin(
		id = DiscordLinkModuleBuildConstants.ID,
		name = DiscordLinkModuleBuildConstants.ID,
		version = DiscordLinkModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class DiscordLinkVelocityModule extends DiscordLinkCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static DiscordLinkVelocityModule instance;

	private @Inject ProxyServer proxy;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject FileManager fileManager;

	private DiscordLinkingBot discordLinkingBot;
	private DiscordLinkerManager discordLinkerManager;
	private DiscordLinkedUserDatabaseLoader discordLinkedUserDatabaseLoader;

	private DiscordLinkConfig config; // exported

	public DiscordLinkVelocityModule() {
		DiscordLinkVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {
		this.config = export(fileManager.load(DiscordLinkConfig.class));

		this.discordLinkingBot = export(new DiscordLinkingBot(SecretsUtils.getEnvironmentVariable("JDA_LINKING_TOKEN")));
		this.discordLinkerManager = export(new DiscordLinkerManager());
		this.discordLinkedUserDatabaseLoader = new DiscordLinkedUserDatabaseLoader();
	}

	@Override
	public void onEnable() {

	}

}
