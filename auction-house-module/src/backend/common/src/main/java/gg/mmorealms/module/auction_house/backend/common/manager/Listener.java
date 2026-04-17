package gg.mmorealms.module.auction_house.backend.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import gg.mmorealms.module.auction_house.backend.common.dto.database.AuctionHouseEntry;
import gg.mmorealms.module.auction_house.common.event.AuctionHouseEntryAddedEvent;
import gg.mmorealms.module.auction_house.common.event.AuctionHouseEntryRemovedEvent;

public class Listener {

	@EventHandler
	public void onAuctionHouseEntryAddedEvent(AuctionHouseEntryAddedEvent event) {
		AuctionHouseBackendModule instance = AuctionHouseBackendModule.instance();
		AuctionHouseEntry entry = instance.fromJson(event.getItem(), AuctionHouseEntry.class);
		Logger.info("Received auction house add event for entry with id " + entry.getId());
		instance.getEntriesManager().addEntryInCache(entry, false);
	}

	@EventHandler
	public void onAuctionHouseEntryRemovedEvent(AuctionHouseEntryRemovedEvent event) {
		Logger.info("Received auction house remove event for entry with id " + event.getItemID());
		AuctionHouseBackendModule.instance().getEntriesManager().deleteEntryFromCache(event.getItemID());
	}
}
