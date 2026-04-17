package gg.mmorealms.module.discord_chat.velocity.dto;

import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.discord_chat.velocity.DiscordChatVelocityModule;
import gg.mmorealms.module.discord_chat.velocity.config.DiscordChatConfig;
import gg.mmorealms.module.discord_chat.velocity.manager.DiscordListener;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Getter
public class DiscordChatBot {

	private final JDA bot;
	private final TextChannel channel;

	@SneakyThrows(InterruptedException.class)
	public DiscordChatBot(String token) throws ModuleException {
		if (token == null ||
				token.isEmpty() ||
				token.equals("DISABLED")) {
			throw new ModuleException(DiscordChatVelocityModule.instance(), "Missing JDA tokens in environment variables for Chat Bot");
		}

		this.bot = JDABuilder.createDefault(token)
				.enableIntents(GatewayIntent.GUILD_MESSAGES)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(new DiscordListener())
				.build()
				.awaitReady();

		DiscordChatConfig config = DiscordChatVelocityModule.instance().getConfig();

		this.channel = bot.getTextChannelById(config.chatSyncChannelID);

		if (this.channel == null) {
			throw new ModuleException(DiscordChatVelocityModule.instance(), "Missing JDA channel in environment variables for Chat Bot");
		}

	}

	public void sendMessage(String message) {
		this.channel.sendMessage(message).queue();
	}

}
