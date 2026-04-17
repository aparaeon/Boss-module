package gg.mmorealms.module.mega_evolution.backend.neoforge;

import gg.mmorealms.module.mega_evolution.MegaEvolutionModuleBuildConstants;
import gg.mmorealms.module.mega_evolution.backend.common.MegaEvolutionBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(MegaEvolutionModuleBuildConstants.ID)
public class MegaEvolutionNeoForgeModule extends MegaEvolutionBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static MegaEvolutionNeoForgeModule instance;

	public MegaEvolutionNeoForgeModule() {
		MegaEvolutionNeoForgeModule.instance = this;
	}

}