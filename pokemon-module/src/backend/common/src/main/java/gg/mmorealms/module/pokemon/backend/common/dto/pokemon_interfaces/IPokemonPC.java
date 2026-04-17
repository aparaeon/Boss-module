package gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces;

import com.google.gson.JsonObject;

public interface IPokemonPC {

	Object getNative();

	JsonObject serialize();

}
