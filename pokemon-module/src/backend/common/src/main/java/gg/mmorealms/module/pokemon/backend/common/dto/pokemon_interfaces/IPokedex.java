package gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces;

import com.google.gson.JsonObject;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public interface IPokedex {

	static IPokedex get(@NotNull ServerPlayer player) {
		return PokemonBackendModule.instance().getPlatformImplementation().getPokedex(player);
	}

	Object getNative();

	JsonObject serialize();

	int getMaxSize();

	int getSize();

}
