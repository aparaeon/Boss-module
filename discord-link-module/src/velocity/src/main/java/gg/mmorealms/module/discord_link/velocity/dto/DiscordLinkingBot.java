package gg.mmorealms.module.discord_link.velocity.dto;

import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.discord_link.velocity.DiscordLinkVelocityModule;
import gg.mmorealms.module.discord_link.velocity.config.DiscordLinkConfig;
import gg.mmorealms.module.discord_link.velocity.manager.DiscordListener;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Getter
public class DiscordLinkingBot {

	private final JDA bot;

	private final Guild guild;
	private final TextChannel linkingChannel;
	private final Role role;

	@SneakyThrows(InterruptedException.class)
	public DiscordLinkingBot(String token) throws ModuleException {
		if (token == null ||
				token.isEmpty() ||
				token.equals("DISABLED")) {
			throw new ModuleException(DiscordLinkVelocityModule.instance(), "Missing JDA tokens in environment variables for Chat Bot");
		}

		this.bot = JDABuilder.createDefault(token)
				.enableIntents(GatewayIntent.GUILD_MESSAGES)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.addEventListeners(new DiscordListener())
				.build()
				.awaitReady();

		DiscordLinkConfig config = DiscordLinkVelocityModule.instance().getConfig();

		this.guild = bot.getGuildById(config.guildID);

		if (guild == null) {
			throw new ModuleException(DiscordLinkVelocityModule.instance(), "Guild for linking bot not found");
		}

		this.linkingChannel = guild.getTextChannelById(config.linkChannelID);

		if (linkingChannel == null) {
			throw new ModuleException(DiscordLinkVelocityModule.instance(), "Linking channel for linking bot not found");
		}

		this.role = guild.getRoleById(config.linkedRoleID);

		if (role == null) {
			throw new ModuleException(DiscordLinkVelocityModule.instance(), "Role for linking bot not found");
		}

		this.registerCommands();
	}

	private void registerCommands() {
		DiscordLinkConfig config = DiscordLinkVelocityModule.instance().getConfig();

		this.guild.updateCommands().addCommands(
				Commands.slash("link", config.lang.linkCommandDescription)
						.addOption(OptionType.STRING, "code", config.lang.codeArgumentDescription)
		).queue();
	}

}
