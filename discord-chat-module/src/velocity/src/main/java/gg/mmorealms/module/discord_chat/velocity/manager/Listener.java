package gg.mmorealms.module.discord_chat.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.module.chat.velocity.dto.LocalChatRequest;
import gg.mmorealms.module.discord_chat.velocity.config.DiscordChatConfig;
import gg.mmorealms.module.discord_chat.velocity.dto.DiscordChatBot;

public class Listener {

	private @Inject DiscordChatBot bot;
	private @Inject DiscordChatConfig config;

	@EventHandler
	public void onPlayerChatEvent(LocalChatRequest event) {
		if (!event.getResult().isAllowed() || event.isPrivate()) {
			return;
		}

		bot.sendMessage(config.lang.discordFormat
				.parse("username", event.getPlayer().getUsername())
				.parse("message", serializeForDiscord(event.getMessage()))
				.parse()
		);
	}

	private String serializeForDiscord(String message) {
		return message
				.replaceAll("(?i)(@[a-zA-Z0-9_]+)", "<$1>");
	}

}
