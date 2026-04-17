package gg.mmorealms.module.auction_house.backend.fabric;

import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import net.fabricmc.api.ModInitializer;

public class AuctionHouseFabricModule extends AuctionHouseBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
