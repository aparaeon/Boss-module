package gg.mmorealms.module.tutorial.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.tutorial.TutorialModuleBuildConstants;
import lombok.Getter;

@Getter
@Module(
		id = TutorialModuleBuildConstants.ID,
		version = TutorialModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = TutorialModuleBuildConstants.DEPENDENCIES
)
public abstract class TutorialCoreModule implements CommonModule {

	public TutorialCoreModule() {
	}

}
