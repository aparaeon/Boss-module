package gg.mmorealms.module.trade.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.trade.TradeModuleBuildConstants;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Module(
		id = TradeModuleBuildConstants.ID,
		version = TradeModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = TradeModuleBuildConstants.DEPENDENCIES
)
public abstract class TradeComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static TradeComonModule instance;

	public TradeComonModule() {
		TradeComonModule.instance = this;
	}
}