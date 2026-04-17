package gg.mmorealms.module.discord_link.velocity.config;

import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;

import java.util.List;

public class DiscordLinkConfig {

	public boolean syncDiscordToMinecraft = false;
	public boolean syncMinecraftToDiscord = true;

	public Long guildID = 1332431737694982209L;
	public Long linkChannelID = 1332431738609336436L;
	public Long linkingChannelID = 1332431738609336436L;
	public Long linkedRoleID = 1352437821004189809L;

	public String codeDictionary = "abcdefghijklmnopqrstuvwxyz0123456789";
	public int codeLength = 6;

	public MessageBuilderList rewardCommands = new MessageBuilderList(List.of());
	public MessageBuilderList nitroRewardsCommands = new MessageBuilderList(List.of());

	public List<Pair2<Long, String>> roleConversions = List.of(
			new Pair2<>(1332431738072465419L, "expert"),
			new Pair2<>(1332431738072465420L, "master"),
			new Pair2<>(1332431738072465421L, "heroic"),
			new Pair2<>(1332431738072465422L, "mythical"),
			new Pair2<>(1332431738072465423L, "divine"),
			new Pair2<>(1332431738072465424L, "arbiter"),
			new Pair2<>(1332431738072465425L, "champion"),
			new Pair2<>(1332431738072465426L, "grandmaster"),
			new Pair2<>(1332431738072465427L, "celestial")
//			new Pair2<>(1332431738093441037L, "helper"),
//			new Pair2<>(1332431738093441038L, "mod"),
//			new Pair2<>(1332431738093441039L, "admin"),
//			new Pair2<>(1332431738093441040L, "head_admin"),
//			new Pair2<>(1332431738093441041L, "dev"),
//			new Pair2<>(1332431738093441042L, "manager"),
//			new Pair2<>(1332431738093441043L, "senior_manager"),
//			new Pair2<>(1332431738110083154L, "owner")
	);

	public Lang lang = new Lang();

	public static class Lang {
		public String discord = """
				
				
				Join our <#5865F2>Discord <reset>community: <aqua><click:open_url:https://discord.gg/mmorealms>https://discord.gg/mmorealms</click>
				
				
				""";
		public String couldNotFindLinkedUser = "Could not find a linked user with the provided username/uuid.";
		public String notLinked = "<red>Your account is not linked with a <#5865F2>Discord <reset> one. Please link your account first <click:run_command:/discord link><aqua>/discord link <green>(Click to execute)</click>";
		public String alreadyReceivedRewards = "<red>You have already received your link rewards.";
		public String linkCommandDescription = "Link your Minecraft account to your Discord account";
		public String codeArgumentDescription = "Code to link your account";
		public String invalidCode = "You need to provide a code. Please use /link in game in order to generate one";
		public String alreadyLinked = "Your discord account is already linked to another minecraft account";
		public String linked = "You have successfully linked your account";
		public String linkedInGame = "<green>You have successfully linked your account with <#5865F2>Discord<reset>!";
		public String lpError = "Your do not have a LuckPerms profile. Please contact an administrator.";
		public String cannotUnlinkSelf = "At this time, you cannot unlink your account from Discord. Please contact a staff member for manual assistance.";
		public MessageBuilder unlinked = new MessageBuilder("Successfully unlinked {target_uuid} from {target_discord_id}");
		public MessageBuilder link = new MessageBuilder("Please go to <underlined><click:open_url:https://discord.com/channels/{guild_id}/{channel_id}><#5865F2>Discord (https://discord.gg/mmorealms)</click><reset> and execute the command <click:copy_to_clipboard:'/link {code}'><aqua>/link {code} <white>(Click to copy)</click> in order to finish the linking process and claim your rewards.");
		public MessageBuilder failedToSyncRole = new MessageBuilder("<yellow>Failed to sync your role {role} with the server.");
		public MessageBuilder syncedRoles = new MessageBuilder("<green>Your roles have been successfully synced with the server!. We have applied roles {roles}");

	}

}
