package gg.mmorealms.module.auction_house.backend.neoforge;

import gg.mmorealms.module.auction_house.AuctionHouseModuleBuildConstants;
import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(AuctionHouseModuleBuildConstants.ID)
public class AuctionHouseNeoForgeModule extends AuctionHouseBackendModule {
	public AuctionHouseNeoForgeModule() {
		this.setup();
	}
}
