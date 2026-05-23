package gg.mmorealms.module.login_rewards.backend.common.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class LoginRewardsBackendConfig {

	public GUI gui = new GUI();
	public Lang lang = new Lang();

	public static class Lang {
		public MessageBuilder dayAlreadyClaimed = new MessageBuilder("<yellow>You already claimed Day {day}.");
		public MessageBuilder notCurrentDay = new MessageBuilder("<red>You can only claim Day {claim_day} right now.");
		public MessageBuilder alreadyClaimedToday = new MessageBuilder("<yellow>You've already claimed today's reward. Come back later.");
		public MessageBuilder notEnoughPlaytime = new MessageBuilder("<red>You haven't played enough today yet ({today}/{required}).");
		public MessageBuilder notEnoughSpace = new MessageBuilder("<red>You need {required_slots} free inventory slots (have {available_slots}).");
		public MessageBuilder claimSent = new MessageBuilder("<green>Claiming Day {day}...");
	}

	public static class GUI {
		public String title = "Daily Rewards";

		public List<Integer> daySlots = List.of(
			0, 1, 2, 3, 4, 5, 6, 7, 8,
			9, 10, 11, 12, 13, 14, 15, 16, 17,
			18, 19, 20, 21, 22, 23, 24, 25, 26
		);

		public GUIButton filler = GUIButton.empty()
			.display(Items.GRAY_STAINED_GLASS_PANE)
			.name(" ");

		public GUIButton claimedDay = GUIButton.empty()
			.display(Items.BOOK)
			.name("<green>Day {day} Claimed")
			.lore(List.of(
				"<gray>Reward day: <white>{reward_day}",
				"<gray>Requirement: <white>{required}",
				" ",
				"{reward_lore}"
			));

		public GUIButton currentClaimableDay = GUIButton.empty()
			.display(Items.CHEST)
			.name("<green><bold>Day {day} Ready")
			.lore(List.of(
				"<gray>Reward day: <white>{reward_day}",
				"<gray>Today: <white>{today}<gray>/<white>{required}",
				"<green>Click to claim this day.",
				" ",
				"{reward_lore}"
			));

		public GUIButton currentLockedDay = GUIButton.empty()
			.display(Items.YELLOW_STAINED_GLASS_PANE)
			.name("<yellow>Day {day}")
			.lore(List.of(
				"<gray>Reward day: <white>{reward_day}",
				"<gray>Today: <white>{today}<gray>/<white>{required}",
				"<gray>Remaining: <white>{remaining}",
				" ",
				"{reward_lore}"
			));

		public GUIButton futureDay = GUIButton.empty()
			.display(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
			.name("<gray>Day {day}")
			.lore(List.of(
				"<gray>Reward day: <white>{reward_day}",
				"<gray>Requirement: <white>{required}",
				" ",
				"{reward_lore}"
			));

		public GUIButton status = GUIButton.empty()
			.display(Items.CLOCK)
			.name("<gold><bold>Daily Streak")
			.lore(List.of(
				"<gray>Earned streak: <white>{streak}",
				"<gray>Today: <white>{today}<gray>/<white>{required}",
				"<gray>Claim window: <white>{claim_window}"
			))
			.position(29);

		public GUIButton streakBrokenStatus = GUIButton.empty()
			.display(Items.ELYTRA)
			.name("<red><bold>Streak Broken")
			.lore(List.of(
				"<gray>Your previous streak was <white>{previous_streak}<gray>.",
				"<gray>Your current streak is <white>{streak}<gray>.",
				"<gray>Claim Day <white>{day}</white> to restart.",
				"<gray>Today: <white>{today}<gray>/<white>{required}"
			))
			.position(29);

		public GUIButton claimAll = GUIButton.empty()
			.display(Items.EMERALD)
			.name("<green><bold>Claim Reward")
			.lore(List.of(
				"<gray>Day: <white>{day}",
				"<green>Click to claim this reward."
			))
			.position(31);

		public GUIButton claimAllLocked = GUIButton.empty()
			.display(Items.GRAY_DYE)
			.name("<red><bold>No Rewards Ready")
			.lore(List.of(
				"<gray>Day: <white>{day}",
				"<gray>Playtime: <white>{today}<gray>/<white>{required}",
				"<gray>Remaining: <white>{remaining}",
				"<gray>Available in: <white>{available_in}"
			))
			.position(31);

		public GUIButton noSpace = GUIButton.empty()
			.display(Items.BARRIER)
			.name("<red><bold>Not Enough Space")
			.lore(List.of(
				"<gray>Need <white>{required_slots}</white> free inventory slots.",
				"<gray>Available: <white>{available_slots}"
			))
			.position(31);

		public GUIButton alreadyClaimed = GUIButton.empty()
			.display(Items.GOLD_INGOT)
			.name("<yellow><bold>Already Claimed")
			.lore(List.of(
				"<gray>Your next reward unlocks in <white>{available_in}<gray>.",
				"<gray>Claim before: <white>{claim_window}"
			))
			.position(31);

		public GUIButton close = GUIButton.empty()
			.display(Items.BARRIER)
			.name("<red>Close")
			.position(33);

		public GUIButton prevPage = GUIButton.empty()
			.display(Items.ARROW)
			.name("<yellow>← Previous Page")
			.position(27);

		public GUIButton nextPage = GUIButton.empty()
			.display(Items.ARROW)
			.name("<yellow>Next Page →")
			.position(35);

		public List<String> noRewardLore = new ArrayList<>(List.of("<gray>No reward details configured."));
	}
}
