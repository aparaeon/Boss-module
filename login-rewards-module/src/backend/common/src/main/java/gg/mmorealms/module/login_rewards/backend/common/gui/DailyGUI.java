package gg.mmorealms.module.login_rewards.backend.common.gui;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import gg.mmorealms.module.core.backend.common.gui.feature.interfaces.IPagedGUI;
import gg.mmorealms.module.core.backend.common.utils.InventoryUtils;
import gg.mmorealms.module.login_rewards.backend.common.LoginrewardsBackendModule;
import gg.mmorealms.module.login_rewards.backend.common.config.LoginRewardsBackendConfig;
import gg.mmorealms.module.login_rewards.common.dto.DailyGuiDay;
import gg.mmorealms.module.login_rewards.common.dto.DayStatus;
import gg.mmorealms.module.login_rewards.common.dto.event.ClaimDailyRewardEvent;
import gg.mmorealms.module.login_rewards.common.dto.event.OpenDailyGUIEvent;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyGUI extends GUI implements IPagedGUI {

	private static final int CHEST_ROWS = 4;
	private static final int SLOTS = CHEST_ROWS * 9;
	private static final int FOOTER_START_SLOT = 27;

	private final OpenDailyGUIEvent event;
	private final LoginRewardsBackendConfig config;
	private boolean notEnoughSlots = false;
	private boolean claiming = false;

	public DailyGUI(User user, OpenDailyGUIEvent event) {
		super(user, new GUISettings()
			.chestSize(CHEST_ROWS)
			.paged(new GUISettings.PagedSettings().enabled(true)));
		this.event = event;
		this.config = LoginrewardsBackendModule.instance().getConfig();
		setPage(initialPage());
	}

	private int initialPage() {
		int perPage = daysPerPage();
		if (perPage <= 0) {
			return 0;
		}
		int claimDay = event.getClaimDay();
		if (claimDay <= 0) {
			return 0;
		}
		int page = (claimDay - 1) / perPage;
		int maxPage = Math.max(0, getPagesCount() - 1);
		return Math.min(page, maxPage);
	}

	@Override
	public void open() {
		super.open();
		Logger.debug("Opened daily GUI for " + event.getPlayerUuid());
	}

	@Override
	public String getTitleString() {
		return config.gui.title
			.replace("{day}", String.valueOf(event.getClaimDay()))
			.replace("{streak}", String.valueOf(event.getStreak()));
	}

	@Override
	public int getPagesCount() {
		int total = event.getDays() == null ? 0 : event.getDays().size();
		int perPage = daysPerPage();
		if (total == 0 || perPage == 0) {
			return 1;
		}
		return (total + perPage - 1) / perPage;
	}

	@Override
	protected void draw() {
		notEnoughSlots = event.getRequiredInventorySpace() > 0
			&& InventoryUtils.getFreeSlots(user) < event.getRequiredInventorySpace();

		for (int slot = 0; slot < SLOTS; slot++) {
			setButton(config.gui.filler, slot);
		}

		drawDays();
		drawFooter();
		drawPagination();
	}

	private void drawDays() {
		List<Integer> slots = daySlots();
		List<DailyGuiDay> days = event.getDays();
		int startIndex = getPage() * slots.size();

		for (int slotIndex = 0; slotIndex < slots.size(); slotIndex++) {
			int dayIndex = startIndex + slotIndex;
			if (dayIndex >= days.size()) {
				break;
			}

			DailyGuiDay day = days.get(dayIndex);
			GUIButton button = setButton(selectDayButtonTemplate(day), slots.get(slotIndex));
			configureDayButton(button, day);
		}
	}

	private void configureDayButton(GUIButton button, DailyGuiDay day) {
		if (!day.isClaimed()) {
			ItemStack itemStack = CodecUtils.deserialize(ItemStack.CODEC, day.getDisplayJson(),
				CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
			if (!itemStack.isEmpty()) {
				button.display(itemStack);
			}
		}

		button.placeholders(dayPlaceholders(day))
			.lore(expandRewardLore(button.getLore(), day.getRewardLore()));

		button.onClick(click -> onDayClick(day));
	}

	private void onDayClick(DailyGuiDay day) {
		DayStatus status = computeStatus(day);
		Logger.info("Daily day click by " + event.getPlayerUuid() + " on day " + day.getDay()
			+ " status=" + status + ".");

		LoginRewardsBackendConfig.Lang lang = config.lang;
		switch (status) {
			case CLAIMED -> user.sendMessage(lang.dayAlreadyClaimed.parse("day", day.getDay()));
			case FUTURE -> user.sendMessage(lang.notCurrentDay.parse("claim_day", event.getClaimDay()));
			case LOCKED_COOLDOWN -> user.sendMessage(lang.alreadyClaimedToday);
			case LOCKED_PLAYTIME -> user.sendMessage(lang.notEnoughPlaytime
				.parse("today", formatDuration(event.getTodayPlaytimeMs()))
				.parse("required", day.getRequiredPlaytime()));
			case LOCKED_INVENTORY -> user.sendMessage(lang.notEnoughSpace
				.parse("required_slots", event.getRequiredInventorySpace())
				.parse("available_slots", InventoryUtils.getFreeSlots(user)));
			case CLAIMABLE -> {
				user.sendMessage(lang.claimSent.parse("day", event.getClaimDay()));
				onClaim();
			}
		}
	}

	private DayStatus computeStatus(DailyGuiDay day) {
		if (day.isClaimed()) {
			return DayStatus.CLAIMED;
		}
		if (day.isClaimable()) {
			return notEnoughSlots ? DayStatus.LOCKED_INVENTORY : DayStatus.CLAIMABLE;
		}
		if (!day.isCurrent()) {
			return DayStatus.FUTURE;
		}
		return event.isAlreadyClaimed() ? DayStatus.LOCKED_COOLDOWN : DayStatus.LOCKED_PLAYTIME;
	}

	private DayStatus currentClaimStatus() {
		if (event.isAlreadyClaimed()) {
			return DayStatus.LOCKED_COOLDOWN;
		}
		if (notEnoughSlots) {
			return DayStatus.LOCKED_INVENTORY;
		}
		if (!event.isCanClaim()) {
			return DayStatus.LOCKED_PLAYTIME;
		}
		return DayStatus.CLAIMABLE;
	}

	private GUIButton selectDayButtonTemplate(DailyGuiDay day) {
		return switch (computeStatus(day)) {
			case CLAIMED -> config.gui.claimedDay;
			case CLAIMABLE -> config.gui.currentClaimableDay;
			case LOCKED_PLAYTIME, LOCKED_COOLDOWN, LOCKED_INVENTORY -> config.gui.currentLockedDay;
			case FUTURE -> config.gui.futureDay;
		};
	}

	private void drawFooter() {
		Map<String, Object> placeholders = commonPlaceholders(event.getClaimDay());
		GUIButton statusButton = setButton(event.isStreakBroken()
			? config.gui.streakBrokenStatus
			: config.gui.status);
		statusButton.placeholders(placeholders);

		DayStatus claimStatus = currentClaimStatus();
		GUIButton claimButton = setButton(selectClaimButtonTemplate(claimStatus));
		claimButton.placeholders(placeholders);
		if (claimStatus.isClickable()) {
			claimButton.onClick(click -> onClaim());
		} else {
			claimButton.onClick(click -> Logger.info("Daily claim click blocked for " + event.getPlayerUuid()
				+ " day " + event.getClaimDay() + " status=" + claimStatus + "."));
		}

		setButton(config.gui.close)
			.onClick(this::closeOnClick);
	}

	private void drawPagination() {
		if (getPage() > 0) {
			setButton(config.gui.prevPage)
				.onClick(this::backPage);
		}
		if (getPage() < getPagesCount() - 1) {
			setButton(config.gui.nextPage)
				.onClick(this::nextPage);
		}
	}

	private GUIButton selectClaimButtonTemplate(DayStatus claimStatus) {
		return switch (claimStatus) {
			case CLAIMABLE -> config.gui.claimAll;
			case LOCKED_COOLDOWN -> config.gui.alreadyClaimed;
			case LOCKED_INVENTORY -> config.gui.noSpace;
			default -> config.gui.claimAllLocked;
		};
	}

	private int daysPerPage() {
		return daySlots().size();
	}

	private List<Integer> daySlots() {
		return config.gui.daySlots.stream()
			.filter(slot -> slot >= 0 && slot < FOOTER_START_SLOT)
			.toList();
	}

	private Map<String, Object> dayPlaceholders(DailyGuiDay day) {
		Map<String, Object> placeholders = commonPlaceholders(day.getDay());
		placeholders.put("reward_day", day.getRewardDay());
		placeholders.put("required", day.getRequiredPlaytime());
		return placeholders;
	}

	private Map<String, Object> commonPlaceholders(int day) {
		long nowMs = System.currentTimeMillis();
		long cooldownRemainingMs = event.getClaimAvailableAtMs() - nowMs;
		long playtimeRemainingMs = event.getRequiredPlaytimeMs() - event.getTodayPlaytimeMs();
		Map<String, Object> placeholders = new HashMap<>();
		placeholders.put("day", day);
		placeholders.put("streak", event.getStreak());
		placeholders.put("previous_streak", event.getPreviousStreak());
		placeholders.put("today", formatDuration(event.getTodayPlaytimeMs()));
		placeholders.put("required", formatDuration(event.getRequiredPlaytimeMs()));
		placeholders.put("remaining", formatCountdown(playtimeRemainingMs));
		placeholders.put("available_in", formatCountdown(Math.max(cooldownRemainingMs, playtimeRemainingMs)));
		placeholders.put("claim_window", formatCountdown(event.getClaimExpiresAtMs() - nowMs));
		placeholders.put("required_slots", event.getRequiredInventorySpace());
		placeholders.put("available_slots", InventoryUtils.getFreeSlots(user));
		return placeholders;
	}

	private static String formatCountdown(long ms) {
		if (ms <= 0L) {
			return "now";
		}
		return formatDuration(ms);
	}

	private static String formatDuration(long ms) {
		// Round down to whole seconds so sub-second residues don't surface as "Xms" in tooltips.
		return Time.seconds(Math.max(0L, ms) / 1000L).toString();
	}

	private void onClaim() {
		Logger.info("Claim button clicked by " + event.getPlayerUuid() + " for day " + event.getClaimDay()
			+ " (canClaim=" + event.isCanClaim() + ", alreadyClaimed=" + event.isAlreadyClaimed()
			+ ", requiredSlots=" + event.getRequiredInventorySpace() + ").");

		if (claiming) {
			return;
		}
		claiming = true;

		if (event.getRequiredInventorySpace() > 0
			&& InventoryUtils.getFreeSlots(user) < event.getRequiredInventorySpace()) {
			claiming = false;
			notEnoughSlots = true;
			refresh();
			return;
		}

		new ClaimDailyRewardEvent(event.getPlayerUuid(), event.getClaimDay()).send();
		close();
	}

	private List<String> expandRewardLore(List<String> templateLore, List<String> rewardLore) {
		List<String> configuredRewardLore = rewardLore == null || rewardLore.isEmpty()
			? config.gui.noRewardLore
			: rewardLore;

		return new MessageBuilderList(templateLore)
			.parse("reward_lore", configuredRewardLore)
			.parse();
	}

}
