package gg.mmorealms.module.kits.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.kits.KitsModuleBuildConstants;

@Module(
		id = KitsModuleBuildConstants.ID,
		version = KitsModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman"},
		dependencies = KitsModuleBuildConstants.DEPENDENCIES
)
public abstract class KitsCommonModule implements CommonModule {
}
