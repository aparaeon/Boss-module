package gg.mmorealms.module.pokemon.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.pokemon.PokemonModuleBuildConstants;

@Module(
		id = PokemonModuleBuildConstants.ID,
		version = PokemonModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = PokemonModuleBuildConstants.DEPENDENCIES
)
public abstract class PokemonCommonModule implements CommonModule {
}
