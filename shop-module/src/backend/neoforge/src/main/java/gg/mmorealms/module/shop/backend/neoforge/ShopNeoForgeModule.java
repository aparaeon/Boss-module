package gg.mmorealms.module.shop.backend.neoforge;

import gg.mmorealms.module.shop.ShopModuleBuildConstants;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(ShopModuleBuildConstants.ID)
public class ShopNeoForgeModule extends ShopBackendModule {
	public ShopNeoForgeModule() {
		this.setup();
	}
}
