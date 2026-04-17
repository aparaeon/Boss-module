package gg.mmorealms.module.homes.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.homes.HomesModuleBuildConstants;

@Module(
		id = HomesModuleBuildConstants.ID,
		version = HomesModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman"},
		dependencies = HomesModuleBuildConstants.DEPENDENCIES
)
public abstract class HomesCommonModule implements CommonModule {
}
