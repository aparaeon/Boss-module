package gg.mmorealms.module.auction_house.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.auction_house.AuctionHouseModuleBuildConstants;

@Module(
		id = AuctionHouseModuleBuildConstants.ID,
		version = AuctionHouseModuleBuildConstants.VERSION,
		authors = {"Radu Voinea", "Andrei-Madalin Coman"},
		dependencies = AuctionHouseModuleBuildConstants.DEPENDENCIES
)
public abstract class AuctionHouseCommonModule implements CommonModule {
}
