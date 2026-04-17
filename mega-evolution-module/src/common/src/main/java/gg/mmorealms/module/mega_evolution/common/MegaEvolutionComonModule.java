package gg.mmorealms.module.mega_evolution.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.mega_evolution.MegaEvolutionModuleBuildConstants;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Module(
		id = MegaEvolutionModuleBuildConstants.ID,
		version = MegaEvolutionModuleBuildConstants.VERSION,
		authors = {"ZeroDelusions"},
		dependencies = MegaEvolutionModuleBuildConstants.DEPENDENCIES
)
public abstract class MegaEvolutionComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static MegaEvolutionComonModule instance;

	public MegaEvolutionComonModule() {
		MegaEvolutionComonModule.instance = this;
	}
}