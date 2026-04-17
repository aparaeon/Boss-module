package gg.mmorealms.module.essentials.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.essentials.EssentialsModuleBuildConstants;
import lombok.Getter;
import lombok.experimental.Accessors;

@Module(
		id = EssentialsModuleBuildConstants.ID,
		version = EssentialsModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman", "Radu Voinea"},
		dependencies = EssentialsModuleBuildConstants.DEPENDENCIES
)
public abstract class EssentialsCommonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	private static EssentialsCommonModule instance;

	public EssentialsCommonModule(){
		EssentialsCommonModule.instance = this;
	}

}
