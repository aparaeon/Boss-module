package gg.mmorealms.module.shop.backend.fabric;

import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import net.fabricmc.api.ModInitializer;

public class ShopFabricModule extends ShopBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
