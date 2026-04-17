package gg.mmorealms.module.mega_evolution.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.mega_evolution.MegaEvolutionModuleBuildConstants;
import gg.mmorealms.module.mega_evolution.common.MegaEvolutionComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = MegaEvolutionModuleBuildConstants.ID,
		name = MegaEvolutionModuleBuildConstants.ID,
		version = MegaEvolutionModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
@Getter
public class MegaEvolutionVelocityModule extends MegaEvolutionComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static MegaEvolutionVelocityModule instance;

	public MegaEvolutionVelocityModule() {
		MegaEvolutionVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}