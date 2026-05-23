package gg.mmorealms.module.auction_house.backend.common.gui;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import gg.mmorealms.module.auction_house.backend.common.config.AuctionHouseConfig;
import gg.mmorealms.module.auction_house.backend.common.dto.AuctionHouseListingEntry;
import gg.mmorealms.module.auction_house.backend.common.dto.database.AuctionHouseEntry;
import gg.mmorealms.module.auction_house.backend.common.exception.RateLimitException;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.backend.common.gui.PriceConfirmationGUI;
import gg.mmorealms.module.economy.common.dto.Price;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class ListingConfirmationGUI<SoldEntry extends AuctionHouseListingEntry> extends PriceConfirmationGUI {

	private final static AuctionHouseConfig CONFIG = AuctionHouseBackendModule.instance().getConfig();

	protected final AuctionHouseEntry.Type entryType;
	protected final SoldEntry soldEntry;
	protected final ItemStack displayItem;
	protected final int price;
	protected final int tax;

	private static int computeTax(int amount) {
		return (int) Math.ceil(amount * CONFIG.listingTaxPercent / 100.0);
	}

	public ListingConfirmationGUI(User user, AuctionHouseEntry.Type entryType, SoldEntry soldEntry, int price) {
		super(user, new Price(computeTax(price), CONFIG.currency));
		this.entryType = entryType;
		this.soldEntry = soldEntry;
		this.displayItem = this.soldEntry.getDisplayItem();
		this.price = price;
		this.tax = computeTax(price);
	}

	@Override
	public String getTitleString() {
		return "Confirm Listing";
	}

	@Override
	public void draw() {
		super.draw();

		String formattedPrice = NumberUtils.formatNumberWithCommas(price);
		String formattedTax = NumberUtils.formatNumberWithCommas(tax);

		GUIButton preview = GUIButton.of(displayItem)
			.position(1, 4)
			.lore(List.of(
				"<white>Asking Price: <green>" + formattedPrice + " <yellow>" + CONFIG.currency.getName(),
				"<white>Tax (" + CONFIG.listingTaxPercent + "%): <red>" + formattedTax + " <yellow>" + CONFIG.currency.getName(),
				"",
				"<red>This tax is non-refundable!",
				"<gray>It will not be returned if the item is removed unsold."
			));

		setButton(preview);
	}

	@Override
	protected void onConfirm() {
		IBalances balances = IBalances.getByUser(user);

		if (!balances.has(CONFIG.currency, tax * 1.0)) {
			user.sendMessage(
				CONFIG.lang.notEnoughMoneyForTax
					.parse("{tax}", NumberUtils.formatNumberWithCommas(tax))
					.parse("{currency}", CONFIG.currency.getName())
			);
			return;
		}

		boolean result = createListing();
		if (!result) {
			return;
		}

		balances.add(CONFIG.currency, -tax * 1.0, "AUCTION_HOUSE_LISTING_TAX");
		user.sendMessage(CONFIG.lang.entryListed);
		close();
	}

	@Override
	protected void onCancel() {
		cancelListing();
	}

	protected boolean createListing() {
		AuctionHouseEntry entry;
		try {
			entry = new AuctionHouseEntry(user, this.entryType, this.soldEntry, price);
			entry.save();
		} catch (DatabaseSaveException exception) {
			Logger.error(exception);
			user.sendMessage("<red>There was an error while trying to create an auction house entry."); // TODO Config
			this.cancelListing();
			return false;
		} catch (RateLimitException exception) {
			Logger.error(exception);
			user.sendMessage(exception.getMessage());
			this.cancelListing();
			return false;
		}

		return true;
	}

	protected abstract void cancelListing();

}
