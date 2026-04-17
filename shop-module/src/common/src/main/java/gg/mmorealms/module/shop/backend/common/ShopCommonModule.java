package gg.mmorealms.module.shop.backend.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.shop.ShopModuleBuildConstants;

@Module(
		id = ShopModuleBuildConstants.ID,
		version = ShopModuleBuildConstants.VERSION,
		authors = {"UnsafeDodo", "Rickiewars", "Andrei-Madalin Coman"},
		dependencies = ShopModuleBuildConstants.DEPENDENCIES
)
public abstract class ShopCommonModule implements CommonModule {

}
