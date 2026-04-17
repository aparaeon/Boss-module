package gg.mmorealms.module.tms.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.tms.TmsModuleBuildConstants;

@Module(
		id = TmsModuleBuildConstants.ID,
		version = TmsModuleBuildConstants.VERSION,
		authors = {"ZeroDelusions"},
		dependencies = TmsModuleBuildConstants.DEPENDENCIES
)
public abstract class TMsCommonModule implements CommonModule {
}
