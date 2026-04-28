package gg.mmorealms.module.chat_games.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.chat_games.ChatGamesModuleBuildConstants;
import gg.mmorealms.module.chat_games.common.ChatGamesCommonModule;
import gg.mmorealms.module.chat_games.velocity.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.velocity.manager.ChatGamesManager;
import gg.mmorealms.module.chat_games.velocity.manager.LeaderboardManager;
import gg.mmorealms.module.chat_games.velocity.manager.SeasonManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
	id = ChatGamesModuleBuildConstants.ID,
	name = ChatGamesModuleBuildConstants.ID,
	version = ChatGamesModuleBuildConstants.VERSION,
	authors = {"Kaioshiyazaki"}
)
public class ChatGamesVelocityModule extends ChatGamesCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static ChatGamesVelocityModule instance;

	private @Inject ProxyServer proxy;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject FileManager fileManager;

	private ChatGamesConfig config;
	private ChatGamesManager chatGamesManager;
	private LeaderboardManager leaderboardManager;
	private SeasonManager seasonManager;

	public ChatGamesVelocityModule() {
		ChatGamesVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(ChatGamesConfig.class));
		export(fileManager);
		this.leaderboardManager = export(new LeaderboardManager());
		this.seasonManager = export(new SeasonManager());
		this.chatGamesManager = export(new ChatGamesManager());

		this.chatGamesManager.start();
	}

	@Override
	public void onEnable() {
		seasonManager.schedule();
		leaderboardManager.refresh();
		leaderboardManager.schedule();
	}

}
