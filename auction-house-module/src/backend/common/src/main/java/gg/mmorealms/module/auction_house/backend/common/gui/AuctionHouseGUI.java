package gg.mmorealms.module.auction_house.backend.common.gui;

import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import gg.mmorealms.module.auction_house.backend.common.config.AuctionHouseConfig;
import gg.mmorealms.module.auction_house.backend.common.dto.AuctionHouseCategory;
import gg.mmorealms.module.auction_house.backend.common.dto.database.AuctionHouseEntry;
import gg.mmorealms.module.auction_house.backend.common.exception.RateLimitException;
import gg.mmorealms.module.auction_house.backend.common.manager.AuctionHouseEntriesManager;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.ConfirmationGUI;
import gg.mmorealms.module.core.backend.common.gui.PagedGUI;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuctionHouseGUI extends PagedGUI {

	private AuctionHouseCategory category;
	private final AuctionHouseConfig config = AuctionHouseBackendModule.instance().getConfig();
	private final AuctionHouseEntriesManager entriesManager = AuctionHouseBackendModule.instance().getEntriesManager();

	public AuctionHouseGUI(User user) {
		this(user, AuctionHouseBackendModule.instance().getConfig().allCategory, 0);
	}

	public AuctionHouseGUI(User user, AuctionHouseCategory category) {
		this(user, category, 0);
	}

	public AuctionHouseGUI(User user, AuctionHouseCategory category, int page) {
		super(user, new Settings().chestSize(6), page);

		this.category = category;

		open();
	}

	@Override
	public String getTitleString() {
		return AuctionHouseBackendModule.instance().getConfig().gui.title;
	}

	@Override
	public void setup() {
		IBalances currencies = IBalances.getByUser(getUser());
		HashMap<String, Object> placeholders = new HashMap<>() {{
			put("currency", AuctionHouseBackendModule.instance().getConfig().currency);
			put("amount", NumberUtils.formatNumberWithUnits(currencies.get(AuctionHouseBackendModule.instance().getConfig().currency)));
			put("user", getUser().getUsername());
		}};

		setButton(config.gui.back)
				.onClick(this::previousPage);
		setButton(config.gui.next)
				.onClick(this::nextPage);

		setFilterButton();
		setButton(config.gui.ownListings)
				.onClick(this::openOwnListings)
				.placeholders(placeholders);
		setButton(config.gui.info)
				.placeholders(placeholders);
		setButton(config.gui.refresh)
				.onClick(this::refresh);
		setButton(config.gui.close).onClick(this::close);

		setEntries();
	}

	private void openOwnListings(ClickType action) {
		this.category = AuctionHouseBackendModule.instance().getConfig().ownCategory;
		refresh();
	}

	private void setFilterButton() {
		List<String> filterLore = new ArrayList<>();
		List<AuctionHouseCategory> categories = AuctionHouseBackendModule.instance().getConfig().getSelectableCategories();

		for (AuctionHouseCategory value : categories) {
			if (value == category) {
				filterLore.add("<white>" + value.getName());
				continue;
			}
			filterLore.add("<gray>" + value.getName());
		}

		int currentFilterIndex = categories.indexOf(category);
		AuctionHouseCategory newCategory = categories.get((currentFilterIndex + 1) % categories.size());

		setButton(AuctionHouseBackendModule.instance().getConfig().gui.filter)
				.lore(filterLore)
				.onClick((click) ->
						new AuctionHouseGUI(getUser(), newCategory)
				);
	}

	private void setEntries() {
		List<Integer> slots = AuctionHouseBackendModule.instance().getConfig().gui.slots;

		List<AuctionHouseEntry> entries;

		try {
			if (category == AuctionHouseBackendModule.instance().getConfig().ownCategory) {
				entries = entriesManager.getForPlayer(this.getUser().getUUID(), this.getPage(), slots.size());
			} else if (category == AuctionHouseBackendModule.instance().getConfig().allCategory) {
				entries = entriesManager.getAll();
			} else {
				entries = entriesManager.getForCategory(category.getName());
			}
		} catch (RateLimitException exception) {
			this.user.sendMessage(exception.getMessage());
			this.close();
			return;
		}

		int start = slots.size() * getPage();
		for (int index = slots.size() * getPage(); index < Math.min(entries.size(), slots.size() * (getPage() + 1)); index++) {
			AuctionHouseEntry entry = entries.get(index);

			ItemStack itemStack = entry.getDisplayItem();
			setButton(slots.get(index - start))
					.displayName(itemStack.getHoverName().getString())
					.display(itemStack)
					.onClick((click) ->
							new ConfirmationGUI(user) {
								@Override
								protected void onConfirm(ClickType click) {
									purchase(click, entry);
								}

								@Override
								protected void onCancel(ClickType click) {
									AuctionHouseGUI.super.open();
								}
							}.open()
					);
		}
	}

	private void purchase(ClickType click, AuctionHouseEntry entry) {
		entry.sell(getUser());
		if (!AuctionHouseBackendModule.hasActiveCooldown(user)) {
			new AuctionHouseGUI(user);
			return;
		}

		this.close();
	}

}
