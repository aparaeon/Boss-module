package gg.mmorealms.module.realms.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.realms.RealmsModuleBuildConstants;

@Module(
		id = RealmsModuleBuildConstants.ID,
		version = RealmsModuleBuildConstants.VERSION,
		authors = {"Radu Voinea", "Andrei-Madalin Coman"},
		dependencies = RealmsModuleBuildConstants.DEPENDENCIES
)
public abstract class RealmsCommonModule implements CommonModule {
}
