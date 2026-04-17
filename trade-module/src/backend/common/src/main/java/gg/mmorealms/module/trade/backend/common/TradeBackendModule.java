package gg.mmorealms.module.trade.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.trade.common.TradeComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class TradeBackendModule extends TradeComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static TradeBackendModule instance;

	public TradeBackendModule() {
		TradeBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}


}