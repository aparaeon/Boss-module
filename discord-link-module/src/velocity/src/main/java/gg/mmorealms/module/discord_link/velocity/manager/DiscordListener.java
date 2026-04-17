package gg.mmorealms.module.discord_link.velocity.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import gg.mmorealms.module.discord_link.velocity.DiscordLinkVelocityModule;
import gg.mmorealms.module.discord_link.velocity.config.DiscordLinkConfig;
import gg.mmorealms.module.discord_link.velocity.database.DiscordLinkedUser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class DiscordListener extends ListenerAdapter {

	private final DiscordLinkConfig config = DiscordLinkVelocityModule.instance().getConfig();

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.getChannel().getIdLong() != DiscordLinkVelocityModule.instance().getDiscordLinkingBot().getLinkingChannel().getIdLong()) {
			return;
		}

		ScheduleUtils.runTaskLater(
				() -> event.getMessage().delete().queue(),
				Time.seconds(5)
		);

		if (event.getAuthor().isBot()) {
			return;
		}

		String message = event.getMessage().getContentRaw();

		if (message.startsWith("/") || message.startsWith("!") || message.startsWith("link")) {
			String[] split = message.split(" ");
			if (split.length < 2) {
				event.getMessage().reply(config.lang.invalidCode)
						.queue();
				return;
			}
			String code = split[1];
			code = code.replace("code:", "");
			Long userID = event.getAuthor().getIdLong();
			LinkResult result = handleLinkCommand(code, userID);

			switch (result) {
				case INVALID_CODE -> event.getMessage().reply(config.lang.invalidCode)
						.queue();
				case ALREADY_LINKED -> event.getMessage().reply(config.lang.alreadyLinked)
						.queue();
				case LINKED -> event.getMessage().reply(config.lang.linked)
						.queue();
			}
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getName().equals("link")) {
			OptionMapping codeOption = event.getOption("code");

			if (codeOption == null) {
				event.reply(config.lang.invalidCode)
						.setEphemeral(true)
						.queue();
				return;
			}

			String code = codeOption.getAsString();
			Long userID = event.getUser().getIdLong();
			LinkResult result = handleLinkCommand(code, userID);

			switch (result) {
				case INVALID_CODE -> event.reply(config.lang.invalidCode)
						.setEphemeral(true)
						.queue();
				case ALREADY_LINKED -> event.reply(config.lang.alreadyLinked)
						.setEphemeral(true)
						.queue();
				case LINKED -> event.reply(config.lang.linked)
						.setEphemeral(true)
						.queue();
			}
		}
	}

	private LinkResult handleLinkCommand(String code, Long userID) {
		DiscordLinkerManager.Link link = DiscordLinkVelocityModule.instance().getDiscordLinkerManager().getLink(code);

		if (link == null) {
			return LinkResult.INVALID_CODE;
		}

		DiscordLinkedUser user = DiscordLinkedUser.getByDiscordID(userID);

		if (user != null) {
			return LinkResult.ALREADY_LINKED;
		}

		link.complete(userID);
		return LinkResult.LINKED;
	}

	private enum LinkResult {
		INVALID_CODE,
		ALREADY_LINKED,
		LINKED
	}

}
