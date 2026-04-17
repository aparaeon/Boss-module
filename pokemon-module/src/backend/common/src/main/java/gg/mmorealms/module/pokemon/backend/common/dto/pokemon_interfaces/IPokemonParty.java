package gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.manager.PokemonPlatformImplementation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface IPokemonParty {
	static IPokemonParty get(@NotNull UUID uuid) {
		return PokemonBackendModule.instance().getPlatformImplementation().getParty(uuid);
	}

	static IPokemonParty get(@NotNull ServerPlayer player) {
		return PokemonBackendModule.instance().getPlatformImplementation().getParty(player);
	}

	static List<JsonObject> serializeParty(IPokemonParty iPokemonParty) {
		List<JsonObject> party = new ArrayList<>();

		for (int index = 0; index < 6; index++) {
			IPokemon pokemon = iPokemonParty.getPokemon(index);

			if (pokemon == null) {
				party.add(new JsonObject());
				continue;
			}

			party.add(pokemon.serialize());
		}

		return party;
	}

	static List<IPokemon> deserializeParty(String party) {
		PokemonPlatformImplementation pokemonImplementation = PokemonBackendModule.instance().getPlatformImplementation();

		JsonArray partyJson = JsonParser.parseString(party).getAsJsonArray();
		List<IPokemon> result = new ArrayList<>();

		for (JsonElement jsonElement : partyJson) {
			IPokemon pokemon = pokemonImplementation.deserializePokemon(jsonElement.getAsJsonObject());
			result.add(pokemon);
		}

		return result;
	}

	Integer getSize();

	Object getNative();

	JsonObject serialize();

	IPokemon getPokemon(int index);

	void setPokemon(int index, @Nullable IPokemon pokemon);

	void add(IPokemon pokemon);

	void add(JsonObject serializedPokemon);
}
