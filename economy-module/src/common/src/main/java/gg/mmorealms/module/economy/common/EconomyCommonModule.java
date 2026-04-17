package gg.mmorealms.module.economy.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.economy.EconomyModuleBuildConstants;

@Module(
		id = EconomyModuleBuildConstants.ID,
		version = EconomyModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman", "Radu Voinea"},
		dependencies = EconomyModuleBuildConstants.DEPENDENCIES
)
public abstract class EconomyCommonModule implements CommonModule {
}
