package gg.mmorealms.module.legendaries.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.legendaries.LegendariesModuleBuildConstants;

@Module(
		id = LegendariesModuleBuildConstants.ID,
		version = LegendariesModuleBuildConstants.VERSION,
		authors = {"ZeroDelusions"},
		dependencies = LegendariesModuleBuildConstants.DEPENDENCIES
)
public abstract class LegendariesCommonModule implements CommonModule {
}
