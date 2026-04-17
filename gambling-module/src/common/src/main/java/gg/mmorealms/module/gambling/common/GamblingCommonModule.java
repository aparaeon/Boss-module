package gg.mmorealms.module.gambling.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.gambling.GamblingModuleBuildConstants;

@Module(
		id = GamblingModuleBuildConstants.ID,
		version = GamblingModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = GamblingModuleBuildConstants.DEPENDENCIES
)
public abstract class GamblingCommonModule implements CommonModule {
}
