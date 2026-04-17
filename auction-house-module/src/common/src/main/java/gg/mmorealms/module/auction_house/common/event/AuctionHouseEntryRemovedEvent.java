package gg.mmorealms.module.auction_house.common.event;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import lombok.Getter;

@Getter
public class AuctionHouseEntryRemovedEvent extends NetworkBroadcast {
	private final Long itemID;

	public AuctionHouseEntryRemovedEvent(Long itemID) {
		this.itemID = itemID;
	}
}
