package gg.mmorealms.module.analytics.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.analytics.common.AnalyticsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class AnalyticsBackendModule extends AnalyticsCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static AnalyticsBackendModule instance;

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}
