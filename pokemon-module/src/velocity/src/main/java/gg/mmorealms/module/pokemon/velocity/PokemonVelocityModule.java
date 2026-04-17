package gg.mmorealms.module.pokemon.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.pokemon.PokemonModuleBuildConstants;
import gg.mmorealms.module.pokemon.common.PokemonCommonModule;

@Plugin(
		id = PokemonModuleBuildConstants.ID,
		name = PokemonModuleBuildConstants.ID,
		version = PokemonModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class PokemonVelocityModule extends PokemonCommonModule implements VelocityModule {

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}

}
