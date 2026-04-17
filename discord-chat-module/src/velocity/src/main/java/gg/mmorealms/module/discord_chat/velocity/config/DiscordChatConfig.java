package gg.mmorealms.module.discord_chat.velocity.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;

public class DiscordChatConfig {

	public Long chatSyncChannelID = 1352076095104487529L;

	public Lang lang = new Lang();

	public static class Lang {
		public MessageBuilder discordToInGameMessageFormat = new MessageBuilder("<#5865F2>[Discord] <#{discord_role_color}>{discord_role} <white>{discord_username}: {message}");
		public MessageBuilder discordFormat = new MessageBuilder("{username}: {message}");
	}

}
