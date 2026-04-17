package gg.mmorealms.module.trade.backend.fabric;

import gg.mmorealms.module.trade.backend.common.TradeBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

public class TradeFabricModule extends TradeBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static TradeFabricModule instance;

	public TradeFabricModule() {
		TradeFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}