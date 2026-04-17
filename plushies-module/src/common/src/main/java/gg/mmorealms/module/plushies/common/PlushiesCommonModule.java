package gg.mmorealms.module.plushies.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.plushies.PlushiesModuleBuildConstants;

@Module(
		id = PlushiesModuleBuildConstants.ID,
		version = PlushiesModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = PlushiesModuleBuildConstants.DEPENDENCIES
)
public abstract class PlushiesCommonModule implements CommonModule {
}
