package gg.mmorealms.module.chat_games.backend.common.gui;

import gg.mmorealms.module.chat_games.backend.common.ChatGamesBackendModule;
import gg.mmorealms.module.chat_games.backend.common.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.common.dto.LeaderboardEntry;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.PagedGUI;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardGUI extends PagedGUI {

	private final List<LeaderboardEntry> overallEntries;
	private final List<LeaderboardEntry> seasonEntries;
	private final boolean season;
	private final Map<Integer, List<String>> seasonRewardLore;

	private LeaderboardGUI(User user, List<LeaderboardEntry> overallEntries,
	                       List<LeaderboardEntry> seasonEntries, boolean season,
	                       Map<Integer, List<String>> seasonRewardLore) {
		super(user, new Settings().chestSize(6));
		this.overallEntries = overallEntries;
		this.seasonEntries = seasonEntries;
		this.season = season;
		this.seasonRewardLore = seasonRewardLore;
		open();
	}

	public static void open(User user, List<LeaderboardEntry> overallEntries,
	                        List<LeaderboardEntry> seasonEntries, boolean season,
	                        Map<Integer, List<String>> seasonRewardLore) {
		new LeaderboardGUI(user, overallEntries, seasonEntries, season, seasonRewardLore);
	}

	private List<LeaderboardEntry> entries() {
		return season ? seasonEntries : overallEntries;
	}

	@Override
	public String getTitleString() {
		return season ? config().leaderboardGUI.seasonTitle : config().leaderboardGUI.title;
	}

	@Override
	public void setup() {
		setupContentBackground();
		setupFooter();

		List<LeaderboardEntry> entries = entries();
		if (entries.isEmpty()) {
			setButton(config().leaderboardGUI.empty);
			return;
		}

		List<Integer> slots = config().leaderboardGUI.slots;
		int startIndex = getPage() * slots.size();
		for (int slotIndex = 0; slotIndex < slots.size(); slotIndex++) {
			int entryIndex = startIndex + slotIndex;
			if (entryIndex >= entries.size()) {
				break;
			}

			LeaderboardEntry entry = entries.get(entryIndex);
			setButton(buildPlacementButton(entry), slots.get(slotIndex));
		}
	}

	@Override
	protected int getPagesCount() {
		List<LeaderboardEntry> entries = entries();
		if (entries.isEmpty()) {
			return 1;
		}
		List<Integer> slots = config().leaderboardGUI.slots;
		return (entries.size() + slots.size() - 1) / slots.size();
	}

	private void setupFooter() {
		for (int slot : config().leaderboardGUI.footerSlots) {
			setButton(config().leaderboardGUI.footerFiller, slot);
		}

		setButton(config().leaderboardGUI.previousPage)
			.onClick(this::previousPage);

		setButton(config().leaderboardGUI.header.clone()
			.placeholder("page", getPage() + 1)
			.placeholder("total", entries().size()));

		setButton(config().leaderboardGUI.close)
			.onClick(this::closeOnClick);

		setButton(config().leaderboardGUI.nextPage)
			.onClick(this::nextPage);

		GUIButton toggleButton = season
			? config().leaderboardGUI.toggleToOverall
			: config().leaderboardGUI.toggleToSeason;

		setButton(toggleButton)
			.onClick(click -> new LeaderboardGUI(user, overallEntries, seasonEntries, !season, seasonRewardLore));

		setButton(config().leaderboardGUI.claimReward)
			.onClick(click -> {
				close();
				CommandExecuteEvent.onProxy("cg claim", user.getUsername()).send();
			});
	}

	private void setupContentBackground() {
		for (int slot : config().leaderboardGUI.slots) {
			setButton(config().leaderboardGUI.contentFiller, slot);
		}
	}

	private ChatGamesConfig config() {
		return ChatGamesBackendModule.instance().getConfig();
	}

	private GUIButton buildPlacementButton(LeaderboardEntry entry) {
		String placementColor = switch (entry.getPlacement()) {
			case 1 -> "<gold>";
			case 2 -> "<gray>";
			case 3 -> "<#cd7f32>";
			default -> "<white>";
		};

		GUIButton button = config().leaderboardGUI.entry.clone()
			.placeholder("placement_color", placementColor)
			.placeholder("placement", entry.getPlacement())
			.placeholder("username", entry.getUsername())
			.placeholder("wins", entry.getWins());

		String username = entry.getUsername();
		if (username != null && username.length() <= 16) {
			button = button.skullOwner(username);
		}

		if (season && seasonRewardLore != null && entry.getPlacement() <= 3) {
			List<String> rewardLines = seasonRewardLore.get(entry.getPlacement());
			if (rewardLines != null && !rewardLines.isEmpty()) {
				List<String> lore = new ArrayList<>(button.getLore());
				lore.add(" ");
				lore.add("<gold>Season Rewards if they win:");
				lore.addAll(rewardLines);
				button = button.lore(lore);
			}
		}

		return button;
	}

}
