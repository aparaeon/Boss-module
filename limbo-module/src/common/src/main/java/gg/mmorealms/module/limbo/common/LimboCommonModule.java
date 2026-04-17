package gg.mmorealms.module.limbo.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.limbo.LimboModuleBuildConstants;
import lombok.Getter;

@Getter
@Module(
		id = LimboModuleBuildConstants.ID,
		version = LimboModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = LimboModuleBuildConstants.DEPENDENCIES
)
public abstract class LimboCommonModule implements CommonModule {

	public LimboCommonModule() {
	}

}
