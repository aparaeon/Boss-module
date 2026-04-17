package gg.mmorealms.module.discord_chat.velocity.manager;

import gg.mmorealms.module.discord_chat.velocity.DiscordChatVelocityModule;
import gg.mmorealms.module.discord_chat.velocity.config.DiscordChatConfig;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DiscordListener extends ListenerAdapter {

	private final DiscordChatConfig config = DiscordChatVelocityModule.instance().getConfig();

	private String getColorSourceRole(Member member) {
		Color color = member.getColor();

		if (color == null) {
			return "";
		}

		for (Role role : member.getRoles()) {
			if (color.equals(role.getColor())) {
				return role.getName();
			}
		}

		return "";
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.getChannel().getIdLong() != DiscordChatVelocityModule.instance().getDiscordChatBot().getChannel().getIdLong() ||
				event.getAuthor().isBot()) {
			return;
		}

		Member member = event.getMember();

		if (member == null) {
			return;
		}

		Color color = member.getColor();

		if (color == null) {
			color = Color.WHITE;
		}

		String roleName = getColorSourceRole(member);
		String colorHex = Integer.toHexString(color.getRed()) +
				Integer.toHexString(color.getGreen()) +
				Integer.toHexString(color.getBlue());

		DiscordChatVelocityModule.instance().getProxy().sendMessage(DiscordChatVelocityModule.instance().getMiniMessageManager().parse(
				config.lang.discordToInGameMessageFormat
						.parse("discord_role_color", colorHex)
						.parse("discord_role", roleName)
						.parse("discord_username", member.getEffectiveName())
						.parse("message", event.getMessage().getContentRaw())
		));
	}

}
