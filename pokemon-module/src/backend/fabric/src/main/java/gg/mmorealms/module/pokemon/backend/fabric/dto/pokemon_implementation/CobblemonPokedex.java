package gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation;

import com.cobblemon.mod.common.api.pokedex.CaughtCount;
import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.google.gson.JsonObject;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokedex;
import lombok.Getter;

@Getter
public class CobblemonPokedex implements IPokedex {

	private final PokedexManager nativePokedex;

	public CobblemonPokedex(PokedexManager nativePokedex) {
		this.nativePokedex = nativePokedex;
	}

	@Override
	public PokedexManager getNative() {
		return nativePokedex;
	}

	@Override
	public JsonObject serialize() {
		return CodecUtils.serialize(PokedexManager.Companion.getCODEC(), nativePokedex);
	}

	@Override
	public int getMaxSize() {
		return PokemonSpecies.INSTANCE.getSpecies().size();
	}

	@Override
	public int getSize() {
		return nativePokedex.getGlobalCalculatedValue(CaughtCount.INSTANCE);
	}
}
