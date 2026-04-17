package gg.mmorealms.module.discord_link.velocity.manager;

import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.discord_link.velocity.DiscordLinkVelocityModule;
import gg.mmorealms.module.discord_link.velocity.config.DiscordLinkConfig;
import gg.mmorealms.module.discord_link.velocity.database.DiscordLinkedUser;
import gg.mmorealms.module.discord_link.velocity.exception.AlreadyLinkedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.query.QueryOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DiscordLinkerManager {

	private static final Random RANDOM = new Random();

	private final List<Link> pendingLink = new ArrayList<>();
	private final DiscordLinkConfig config = DiscordLinkVelocityModule.instance().getConfig();

	public @NotNull Link initializeLink(UUID uuid) throws AlreadyLinkedException {
		DiscordLinkedUser user = DiscordLinkedUser.getByUUID(uuid);

		if (user != null) {
			throw new AlreadyLinkedException();
		}

		Link link = getLink(uuid);

		if (link != null) {
			return link;
		}

		String code = generateCode();
		link = new Link(code, uuid);

		pendingLink.add(link);

		return link;
	}

	private @Nullable Link getLink(UUID uuid) {
		for (Link link : pendingLink) {
			if (link.getUuid().equals(uuid)) {
				return link;
			}
		}

		return null;
	}

	public @Nullable Link getLink(String code) {
		for (Link link : pendingLink) {
			if (link.getCode().equals(code)) {
				return link;
			}
		}

		return null;
	}

	private String generateCode() {
		StringBuilder code = new StringBuilder(config.codeLength);

		for (int i = 0; i < config.codeLength; i++) {
			int index = RANDOM.nextInt(config.codeDictionary.length());
			code.append(config.codeDictionary.charAt(index));
		}

		return code.toString();
	}

	public void award(DiscordLinkedUser user) {
		ScheduleUtils.runTaskAsync(() -> {
			boolean result = awardSync(user);
			Player player = user.getPlayer();

			if (!result && player != null) {
				player.sendMessage(DiscordLinkVelocityModule.instance().getMiniMessageManager().parse(config.lang.alreadyReceivedRewards));
			}
		});
	}

	public boolean awardSync(DiscordLinkedUser user) {
		Player player = user.getPlayer();
		Member member = user.getDiscordMember();

		if (player == null || member == null) {
			Logger.warn(new MessageBuilder("""
					Failed to award user {user}.
					player == null? {flag1}
					member == null? {flag2}""")
					.parse("user", user.getUuid())
					.parse("flag1", player == null)
					.parse("flag2", member == null)
			);
			return false;
		}

		boolean awardNormalResult = awardNormal(user, player, member);
		boolean awardNitroResult = awardNitro(user, player, member);
		setNameAndRole(player, member);
		syncRanks(user);

		// TODO make it so the already linked players do not see this
		player.sendMessage(DiscordLinkVelocityModule.instance().getMiniMessageManager().parse(config.lang.linkedInGame));

		if (awardNormalResult || awardNitroResult) {
			try {
				user.save();
			} catch (DatabaseSaveException e) {
				Logger.error(e);
				return false;
			}
			return true;
		}

		return false;
	}

	public void setNameAndRole(Player player, Member member) {
		Guild guild = DiscordLinkVelocityModule.instance().getDiscordLinkingBot().getGuild();

		guild.addRoleToMember(member, DiscordLinkVelocityModule.instance().getDiscordLinkingBot().getRole()).queue();
		guild.modifyNickname(member, player.getUsername()).queue();
	}

	public boolean awardNormal(DiscordLinkedUser user, Player player, Member member) {
		if (user.isClaimedRewards()) {
			return false;
		}

		Logger.info(new MessageBuilder("{user}({uuid}) received the normal discord rewards")
				.parse("user", player.getUsername())
				.parse("uuid", user.getUuid().toString())
		);

		List<String> commands = DiscordLinkVelocityModule.instance().getConfig().rewardCommands
				.parse("user", player.getUsername())
				.parse();

		for (String rewardCommand : commands) {
			DiscordLinkVelocityModule.instance().getProxy().getCommandManager().executeAsync(
					DiscordLinkVelocityModule.instance().getProxy().getConsoleCommandSource(),
					rewardCommand
			);
		}

		user.setClaimedRewards(true);
		return true;
	}

	public boolean awardNitro(DiscordLinkedUser user, Player player, Member member) {
		if (user.isClaimedNitroRewards()) {
			return false;
		}

		if (!member.isBoosting()) {
			return false;
		}

		Logger.info(new MessageBuilder("{user}({uuid}) received the nitro discord rewards")
				.parse("user", player.getUsername())
				.parse("uuid", user.getUuid().toString())
		);

		List<String> nitroCommands = DiscordLinkVelocityModule.instance().getConfig().nitroRewardsCommands
				.parse("user", player.getUsername())
				.parse();

		for (String rewardCommand : nitroCommands) {
			DiscordLinkVelocityModule.instance().getProxy().getCommandManager().executeAsync(
					DiscordLinkVelocityModule.instance().getProxy().getConsoleCommandSource(),
					rewardCommand
			);
		}

		user.setClaimedNitroRewards(true);
		return true;
	}

	public void syncRanks(@Nullable DiscordLinkedUser user) {
		if (user == null) {
			Logger.debug("DiscordLinkedUser is null.");
			return;
		}

		Player player = user.getPlayer();
		Member member = user.getDiscordMember();

		if (player == null || member == null) {
			Logger.debug(new MessageBuilder("""
					Failed to sync ranks for user {user}.
					player == null? {flag1}
					member == null? {flag2}""")
					.parse("user", user.getUuid())
					.parse("flag1", player == null)
					.parse("flag2", member == null)
			);
			return;
		}

		User lpUser = LuckPermsUtils.getUser(player.getUniqueId());

		if (lpUser == null) {
			player.sendMessage(DiscordLinkVelocityModule.instance().getMiniMessageManager().parse(config.lang.lpError));
			return;
		}

		if (config.syncMinecraftToDiscord) {
			syncRanksMinecraftToDiscord(member, lpUser);
		}
		if (config.syncDiscordToMinecraft) {
			syncRanksDiscordToMinecraft(member, lpUser);
		}
	}

	public void syncRanksMinecraftToDiscord(@NotNull Member member, @NotNull User lpUser) {
		for (Group inheritedGroup : lpUser.getInheritedGroups(QueryOptions.defaultContextualOptions())) {
			Logger.debug(new MessageBuilder("Syncing group {group} to discord")
					.parse("group", inheritedGroup.getName())
			);

			Long roleID = null;

			for (Pair2<Long, String> roleConversion : DiscordLinkVelocityModule.instance().getConfig().roleConversions) {
				if (inheritedGroup.getName().equals(roleConversion.second())) {
					roleID = roleConversion.first();
					break;
				}
			}

			if (roleID == null) {
				Logger.debug(new MessageBuilder("Skipping group {group} as it has no discord role conversion")
						.parse("group", inheritedGroup.getName())
				);
				continue;
			}

			Role role = DiscordLinkVelocityModule.instance().getDiscordLinkingBot().getGuild().getRoleById(roleID);

			if (role == null) {
				Logger.warn(
						new MessageBuilder("Failed to find discord role {role}")
								.parse("role", roleID)
				);
				continue;
			}

			DiscordLinkVelocityModule.instance().getDiscordLinkingBot().getGuild().addRoleToMember(member, role).submit();
		}
	}

	public void syncRanksDiscordToMinecraft(@NotNull Member member, @NotNull User lpUser) {
		for (Role role : member.getRoles()) {
			String luckPermsGroup = null;

			for (Pair2<Long, String> roleConversion : DiscordLinkVelocityModule.instance().getConfig().roleConversions) {
				if (role.getIdLong() == roleConversion.first()) {
					luckPermsGroup = roleConversion.second();
					break;
				}
			}

			if (luckPermsGroup == null) {
				continue;
			}

			InheritanceNode node = InheritanceNode.builder(luckPermsGroup).build();
			lpUser.data().add(node);
		}
	}

	@AllArgsConstructor
	@Getter
	public static class Link {
		private String code;
		private UUID uuid;

		public void complete(Long discordID) {
			new DiscordLinkedUser(uuid, discordID);
		}
	}

}
