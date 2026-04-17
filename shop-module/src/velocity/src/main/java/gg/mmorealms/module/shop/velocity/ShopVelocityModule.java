package gg.mmorealms.module.shop.velocity;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.shop.ShopModuleBuildConstants;
import gg.mmorealms.module.shop.backend.common.ShopCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = ShopModuleBuildConstants.ID,
		name = ShopModuleBuildConstants.ID,
		version = ShopModuleBuildConstants.VERSION,
		authors = {"UnsafeDodo", "Rickiewars", "Andrei-Madalin Coman"}
)
public class ShopVelocityModule extends ShopCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static ShopVelocityModule instance;

	private @Inject ProxyServer proxy;

	public ShopVelocityModule() {
		ShopVelocityModule.instance = this;
	}

	@Override
	public void onInit() {

	}

	@Override
	public void onEnable() {
	}
}
