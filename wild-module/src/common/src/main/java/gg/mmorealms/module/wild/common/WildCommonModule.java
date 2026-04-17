package gg.mmorealms.module.wild.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.wild.WildModuleBuildConstants;
import lombok.Getter;

@Getter
@Module(
		id = WildModuleBuildConstants.ID,
		version = WildModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = WildModuleBuildConstants.DEPENDENCIES
)
public abstract class WildCommonModule implements CommonModule {

}
