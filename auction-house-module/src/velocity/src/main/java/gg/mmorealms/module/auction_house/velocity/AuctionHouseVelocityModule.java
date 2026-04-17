package gg.mmorealms.module.auction_house.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.auction_house.AuctionHouseModuleBuildConstants;
import gg.mmorealms.module.auction_house.common.AuctionHouseCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = AuctionHouseModuleBuildConstants.ID,
		name = AuctionHouseModuleBuildConstants.ID,
		version = AuctionHouseModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class AuctionHouseVelocityModule extends AuctionHouseCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static AuctionHouseVelocityModule instance;

	public AuctionHouseVelocityModule() {
		AuctionHouseVelocityModule.instance = this;
	}

	@Override
	public void onInit() {

	}

	@Override
	public void onEnable() {
	}
}
