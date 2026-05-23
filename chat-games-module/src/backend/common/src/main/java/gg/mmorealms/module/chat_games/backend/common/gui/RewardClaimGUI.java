package gg.mmorealms.module.chat_games.backend.common.gui;

import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.module.chat_games.backend.common.ChatGamesBackendModule;
import gg.mmorealms.module.chat_games.backend.common.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.common.dto.ChatGamesPendingReward;
import gg.mmorealms.module.chat_games.common.dto.event.OpenRewardClaimEvent;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import gg.mmorealms.module.core.backend.common.utils.InventoryUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RewardClaimGUI extends GUI {

	private final UUID playerUuid;
	private final int seasonId;
	private final int placement;
	private final long wins;
	private final int requiredSlots;
	private final List<String> displayItems;
	private final List<String> commands;
	private final List<String> rewardLore;
	private boolean notEnoughSlots = false;
	private boolean claiming = false;

	public RewardClaimGUI(User user, OpenRewardClaimEvent event) {
		super(user, new GUISettings().chestSize(4));
		this.playerUuid = event.getPlayerUuid();
		this.seasonId = event.getSeasonId();
		this.placement = event.getPlacement();
		this.wins = event.getWins();
		this.requiredSlots = event.getRequiredSlots();
		this.displayItems = event.getDisplayItems() != null ? event.getDisplayItems() : List.of();
		this.commands = event.getCommands();
		this.rewardLore = event.getRewardLore() != null ? event.getRewardLore() : List.of();
	}

	@Override
	public String getTitleString() {
		return config().claimGUI.title.replace("{placement}", String.valueOf(placement));
	}

	@Override
	public void draw() {
		if (requiredSlots > 0 && InventoryUtils.getFreeSlots(user) < requiredSlots) {
			notEnoughSlots = true;
		}

		// Fill all slots
		for (int slot = 0; slot < 36; slot++) {
			setButton(config().claimGUI.filler, slot);
		}

		// Row 1 (slots 9-17): display reward item sprites centered, or dedicated rewards item
		setDisplayItems();
		if (displayItems.isEmpty() && !rewardLore.isEmpty()) {
			List<String> rewardsLore = new ArrayList<>(rewardLore);
			setButton(config().claimGUI.rewardDisplay.copy().lore(rewardsLore), 13);
		}

		// Row 2 (slots 18-26): action buttons
		// Slot 19: close
		setButton(config().claimGUI.closeButton, 19)
			.onClick(this::closeOnClick);

		// Slot 21: slots needed (if any)
		if (requiredSlots > 0) {
			int freeSlots = InventoryUtils.getFreeSlots(user);
			setButton(config().claimGUI.slotsNeeded.copy()
				.placeholder("required", requiredSlots)
				.placeholder("available", freeSlots), 21);
		}

		// Slot 22: reward info (center)
		List<String> infoLore = new ArrayList<>(config().claimGUI.rewardInfo.getLore());
		if (!rewardLore.isEmpty()) {
			infoLore.add(" ");
			infoLore.add("<gold>Your Rewards:");
			infoLore.addAll(rewardLore);
		}
		setButton(config().claimGUI.rewardInfo.copy()
			.placeholder("placement", placement)
			.placeholder("wins", wins)
			.lore(infoLore), 22);

		// Slot 25: claim or no-space button
		if (notEnoughSlots) {
			int freeSlots = InventoryUtils.getFreeSlots(user);
			setButton(config().claimGUI.noSpaceButton.copy()
				.placeholder("required", requiredSlots)
				.placeholder("available", freeSlots), 25);
		} else {
			setButton(config().claimGUI.claimButton, 25)
				.onClick(this::onClaim);
		}
	}

	private void setDisplayItems() {
		if (displayItems.isEmpty()) {
			return;
		}

		int count = Math.min(displayItems.size(), 9);
		int startSlot = 9 + (9 - count) / 2;

		for (int i = 0; i < count; i++) {
			try {
				ItemStack itemStack = new ItemStack(
					BuiltInRegistries.ITEM.get(ResourceLocation.parse(displayItems.get(i)))
				);
				setButton(startSlot + i)
					.name(itemStack.getHoverName().getString())
					.display(itemStack, true);
			} catch (Exception ignored) {
			}
		}
	}

	private void onClaim(ClickType clickType) {
		if (claiming) {
			return;
		}
		claiming = true;

		if (requiredSlots > 0 && InventoryUtils.getFreeSlots(user) < requiredSlots) {
			claiming = false;
			notEnoughSlots = true;
			refresh();
			return;
		}

		MinecraftServer server = BackendLoader.instance().getServer();
		CommandSourceStack commandSourceStack = server.createCommandSourceStack();
		Logger.info("[ChatGames] Executing " + commands.size() + " reward commands for player "
			+ playerUuid + " (season=" + seasonId + ", placement=" + placement + ")");
		for (String command : commands) {
			Logger.debug("[ChatGames] Executing reward command: " + command);
			server.getCommands().performPrefixedCommand(commandSourceStack, command);
		}

		ScheduleUtils.runTaskAsync(() -> ChatGamesPendingReward.markClaimed(playerUuid, seasonId));

		user.sendMessage(config().claimGUI.claimedMessage);
		close();
	}

	private ChatGamesConfig config() {
		return ChatGamesBackendModule.instance().getConfig();
	}

}
