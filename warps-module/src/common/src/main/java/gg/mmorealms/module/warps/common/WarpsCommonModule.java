package gg.mmorealms.module.warps.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.warps.WarpsModuleBuildConstants;
import lombok.Getter;

@Getter
@Module(
		id = WarpsModuleBuildConstants.ID,
		version = WarpsModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman"},
		dependencies = WarpsModuleBuildConstants.DEPENDENCIES
)
public abstract class WarpsCommonModule implements CommonModule {

}
