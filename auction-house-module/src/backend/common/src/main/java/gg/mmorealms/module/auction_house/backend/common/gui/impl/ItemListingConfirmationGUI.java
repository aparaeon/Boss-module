package gg.mmorealms.module.auction_house.backend.common.gui.impl;

import gg.mmorealms.module.auction_house.backend.common.dto.AuctionHouseListingEntry;
import gg.mmorealms.module.auction_house.backend.common.dto.database.AuctionHouseEntry;
import gg.mmorealms.module.auction_house.backend.common.gui.ListingConfirmationGUI;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ItemListingConfirmationGUI extends ListingConfirmationGUI<AuctionHouseListingEntry.Item> {

	public ItemListingConfirmationGUI(User user, ItemStack soldItem, int price) {
		super(user, AuctionHouseEntry.Type.ITEM, new AuctionHouseListingEntry.Item(soldItem), price);
	}

	@Override
	protected void cancelListing() {
		user.getPlayer().setItemInHand(InteractionHand.MAIN_HAND, this.soldEntry.itemStack());
	}
}
