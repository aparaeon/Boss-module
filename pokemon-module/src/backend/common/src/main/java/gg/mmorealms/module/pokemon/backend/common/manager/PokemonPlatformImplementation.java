package gg.mmorealms.module.pokemon.backend.common.manager;

import com.google.gson.JsonObject;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokedex;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonPC;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;


public abstract class PokemonPlatformImplementation {

	public abstract IPokemon create(String species, boolean shiny);

	public abstract IPokemon fromProperties(String properties);

	public abstract IPokemon getPokemonFromEntity(Entity entity);

	public abstract IPokemon getPokemonFromItem(ItemStack itemStack);

	public abstract IPokedex createEmptyPokedex(UUID uuid);

	public abstract IPokemonParty getParty(UUID uuid);

	public IPokemonParty getParty(ServerPlayer player) {
		return getParty(player.getUUID());
	}

	public abstract IPokemonPC getPC(ServerPlayer player);

	public abstract IPokedex getPokedex(ServerPlayer player);

	public abstract IPokemon deserializePokemon(JsonObject json);

	public abstract IPokemonParty deserializeParty(UUID owner, JsonObject json);

	public abstract IPokemonPC deserializePC(UUID owner, JsonObject json);

	public abstract IPokedex deserializePokedex(UUID owner, JsonObject json);

	public abstract Class<?> getNativePokemonClass();

	public abstract Class<?> getNativePokedexClass();

	public abstract Class<?> getNativePartyClass();

	public abstract Class<?> getNativePCClass();

	public abstract Class<? extends Entity> getNativePokemonEntityClass();

}
