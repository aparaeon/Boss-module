package gg.mmorealms.module.hunts.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.hunts.HuntsModuleBuildConstants;

@Module(
        id = HuntsModuleBuildConstants.ID,
		version = HuntsModuleBuildConstants.VERSION,
        authors = {"ZeroDelusions"},
        dependencies = HuntsModuleBuildConstants.DEPENDENCIES
)
public abstract class HuntsCommonModule implements CommonModule {
}
