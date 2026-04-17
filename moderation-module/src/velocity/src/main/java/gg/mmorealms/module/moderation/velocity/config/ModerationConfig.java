package gg.mmorealms.module.moderation.velocity.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;

public class ModerationConfig {

	public Lang lang = new Lang();

	public static class Lang {
		public String userNotFound = "<red>User not found.";
		public String punishmentApplied = "<green>Punishment applied";
		public String muteRevoked = "<green>Your mute has been revoked";
		public String punishmentRevoked = "<green>Punishment revoked";
		public MessageBuilder kickReasonFormat = new MessageBuilder("<red>{reason}");

		public MessageBuilder banMessage = new MessageBuilder("""
				<red>You have been banned for <white>{remaining} <red>for <white>{reason}.
				
				<yellow>You can appeal at <aqua>https://discord.gg/mmorealms""");
		public MessageBuilder muteMessage = new MessageBuilder("<red>You have been muted for <white>{remaining} <red>for <white>{reason}");
		public MessageBuilder warnMessage = new MessageBuilder("<red>You have been warned <red>for <white>{reason}");

		public String active = "<red> ({remaining} remaining)";
		public String inactive = "<gray> (inactive)";

		public MessageBuilder banEntry = new MessageBuilder("<red> [BAN]  <white>{date} <red>by <white>{staff} <red>for <aqua>{reason} {active}");
		public MessageBuilder muteEntry = new MessageBuilder("<gold> [MUTE] <white>{date} <gold>by <white>{staff} <gold>for <aqua>{reason} {active}");
		public MessageBuilder warnEntry = new MessageBuilder("<yellow> [WARN] <white>{date} <yellow>by <white>{staff} <yellow>for <aqua>{reason} {active}");

		public MessageBuilder banBroadcast = new MessageBuilder("""
				
				<white>============== <aqua>MMORealms Moderation - <red>BAN <white>==============
				<red>▶ <white>Punished: <aqua>{target}
				<red>▶ <white>Staff: <aqua>{staff}
				<red>▶ <white>Reason: <aqua>{reason}
				<red>▶ <white>Duration: <aqua>{duration}
				<white>====================================================
				
				""");
		public MessageBuilder muteBroadcast = new MessageBuilder("""
				
				<white>============= <aqua>MMORealms Moderation - <red>MUTE <white>==============
				<red>▶ <white>Punished: <aqua>{target}
				<red>▶ <white>Staff: <aqua>{staff}
				<red>▶ <white>Reason: <aqua>{reason}
				<red>▶ <white>Duration: <aqua>{duration}
				<white>=====================================================
				
				""");

		public MessageBuilder warnBroadcast = new MessageBuilder("""
				
				<white>============= <aqua>MMORealms Moderation - <red>WARN <white>==============
				<red>▶ <white>Punished: <aqua>{target}
				<red>▶ <white>Staff: <aqua>{staff}
				<red>▶ <white>Reason: <aqua>{reason}
				<red>▶ <white>Duration: <aqua>{duration}
				<white>=====================================================
				
				""");
		public MessageBuilder kickBroadcast = new MessageBuilder("""
				
				<white>============= <aqua>MMORealms Moderation - <red>KICK <white>==============
				<red>▶ <white>Punished: <aqua>{target}
				<red>▶ <white>Staff: <aqua>{staff}
				<red>▶ <white>Reason: <aqua>{reason}
				<white>=====================================================
				
				""");
	}

}
