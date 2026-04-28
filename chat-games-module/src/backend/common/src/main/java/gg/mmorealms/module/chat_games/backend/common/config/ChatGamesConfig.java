package gg.mmorealms.module.chat_games.backend.common.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import net.minecraft.world.item.Items;

import java.util.List;

public class ChatGamesConfig {

	public LeaderboardGUI leaderboardGUI = new LeaderboardGUI();
	public ClaimGUI claimGUI = new ClaimGUI();

	public static class LeaderboardGUI {
		public String title = "All-Time Leaderboard";
		public String seasonTitle = "Monthly Leaderboard";

		public List<Integer> slots = List.of(
			0, 1, 2, 3, 4, 5, 6, 7, 8,
			9, 10, 11, 12, 13, 14, 15, 16, 17,
			18, 19, 20, 21, 22, 23, 24, 25, 26,
			27, 28, 29, 30, 31, 32, 33, 34, 35
		);

		public GUIButton contentFiller = new GUIButton()
			.display(Items.BLACK_STAINED_GLASS_PANE)
			.displayName(" ");

		public List<Integer> footerSlots = List.of(45, 46, 47, 48, 49, 50, 51, 52, 53);

		public GUIButton footerFiller = new GUIButton()
			.display(Items.GRAY_STAINED_GLASS_PANE)
			.displayName(" ");

		public GUIButton previousPage = new GUIButton()
			.display(Items.ARROW)
			.displayName("<gray>Previous Page")
			.position(45);

		public GUIButton nextPage = new GUIButton()
			.display(Items.ARROW)
			.displayName("<gray>Next Page")
			.position(53);

		public GUIButton close = new GUIButton()
			.display(Items.BARRIER)
			.displayName("<red>Close")
			.position(50);

		public GUIButton header = new GUIButton()
			.display(Items.NAME_TAG)
			.displayName("<light_purple><bold>✦ Chat Games Leaderboard")
			.lore(List.of("<gray>Page: <white>{page}", "<gray>Total Entries: <white>{total}"))
			.position(49);

		public GUIButton empty = new GUIButton()
			.display(Items.BARRIER)
			.displayName("<red>No leaderboard entries yet")
			.lore(List.of("<gray>Play chat games to appear here."))
			.position(22);

		public GUIButton entry = new GUIButton()
			.display(Items.PLAYER_HEAD)
			.displayName("{placement_color}#{placement} <white>{username}")
			.lore(List.of("<gray>Wins: <white>{wins}"));

		public GUIButton toggleToSeason = new GUIButton()
			.display(Items.CLOCK)
			.displayName("<aqua>☽ Season View")
			.lore(List.of("<gray>Click to view the season leaderboard"))
			.position(48);

		public GUIButton toggleToOverall = new GUIButton()
			.display(Items.COMPASS)
			.displayName("<aqua>✦ Overall View")
			.lore(List.of("<gray>Click to view the overall leaderboard"))
			.position(48);

		public GUIButton claimReward = new GUIButton()
			.display(Items.NETHER_STAR)
			.displayName("<gold>✦ Claim Season Reward")
			.lore(List.of("<gray>Click to claim your reward", "<gray>if you placed in the top 3!"))
			.position(51);
	}

	public static class ClaimGUI {
		public String title = "Season Reward";

		public GUIButton filler = new GUIButton()
			.display(Items.GRAY_STAINED_GLASS_PANE)
			.displayName(" ");

		public GUIButton rewardInfo = new GUIButton()
			.display(Items.NETHER_STAR)
			.displayName("<gold><bold>✦ Season Reward")
			.lore(List.of(
				"<gray>Placement: <gold>#{placement}",
				"<gray>Wins: <white>{wins}",
				" ",
				"<gray>Claim your reward to receive your prize!"
			));

		public GUIButton slotsNeeded = new GUIButton()
			.display(Items.CHEST)
			.displayName("<yellow>Inventory Space Needed")
			.lore(List.of(
				"<gray>Required: <white>{required} free slot(s)",
				"<gray>Available: <white>{available} free slot(s)"
			));

		public GUIButton claimButton = new GUIButton()
			.display(Items.GREEN_STAINED_GLASS_PANE)
			.displayName("<bold><green>✓ Claim Reward")
			.lore(List.of("<gray>Click to collect your prize!"));

		public GUIButton noSpaceButton = new GUIButton()
			.display(Items.RED_STAINED_GLASS_PANE)
			.displayName("<bold><red>✗ Not Enough Space")
			.lore(List.of(
				"<red>You need <white>{required}</white> free slot(s).",
				"<red>You only have <white>{available}</white> free slot(s).",
				" ",
				"<gray>Free up space and try again."
			));

		public GUIButton rewardDisplay = new GUIButton()
			.display(Items.EMERALD)
			.displayName("<gold><bold>★ Your Rewards");

		public GUIButton closeButton = new GUIButton()
			.display(Items.BARRIER)
			.displayName("<red>Close");

		public MessageBuilder claimedMessage = new MessageBuilder(
			"<green>You have successfully claimed your season reward!"
		);
	}

}