package gg.mmorealms.module.trade.backend.neoforge;

import gg.mmorealms.module.trade.TradeModuleBuildConstants;
import gg.mmorealms.module.trade.backend.common.TradeBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(TradeModuleBuildConstants.ID)
public class TradeNeoForgeModule extends TradeBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static TradeNeoForgeModule instance;

	public TradeNeoForgeModule() {
		TradeNeoForgeModule.instance = this;
	}

}