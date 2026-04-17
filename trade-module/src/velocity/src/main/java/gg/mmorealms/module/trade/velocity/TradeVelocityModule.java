package gg.mmorealms.module.trade.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.trade.TradeModuleBuildConstants;
import gg.mmorealms.module.trade.common.TradeComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = TradeModuleBuildConstants.ID,
		name = TradeModuleBuildConstants.ID,
		version = TradeModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
@Getter
public class TradeVelocityModule extends TradeComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static TradeVelocityModule instance;

	public TradeVelocityModule() {
		TradeVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}