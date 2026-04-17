package gg.mmorealms.module.auction_house.velocity.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.module.auction_house.common.event.AuctionHouseEntryAddedEvent;
import gg.mmorealms.module.auction_house.common.event.AuctionHouseEntryRemovedEvent;

public class Listener {
	@EventHandler
	public void onAuctionHouseEntryAddedEvent(AuctionHouseEntryAddedEvent event) {
	}

	@EventHandler
	public void onAuctionHouseEntryRemovedEvent(AuctionHouseEntryRemovedEvent event) {
	}
}
