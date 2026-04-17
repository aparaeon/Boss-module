package gg.mmorealms.module.crates.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.crates.CratesModuleBuildConstants;

@Module(
		id = CratesModuleBuildConstants.ID,
		version = CratesModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = CratesModuleBuildConstants.DEPENDENCIES
)
public abstract class CratesCommonModule implements CommonModule {
}
