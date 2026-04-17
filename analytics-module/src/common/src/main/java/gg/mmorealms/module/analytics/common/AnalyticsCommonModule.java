package gg.mmorealms.module.analytics.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.analytics.AnalyticsModuleBuildConstants;
import lombok.Getter;

@Getter
@Module(
		id = AnalyticsModuleBuildConstants.ID,
		version = AnalyticsModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = AnalyticsModuleBuildConstants.DEPENDENCIES
)
public abstract class AnalyticsCommonModule implements CommonModule {

	public AnalyticsCommonModule() {
	}

}
