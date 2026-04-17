package gg.mmorealms.module.auction_house.common.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import lombok.Getter;

@Getter
public class AuctionHouseEntryAddedEvent extends NetworkBroadcast {
	private final String item;

	public AuctionHouseEntryAddedEvent(String item) {
		this.item = item;
	}
}
