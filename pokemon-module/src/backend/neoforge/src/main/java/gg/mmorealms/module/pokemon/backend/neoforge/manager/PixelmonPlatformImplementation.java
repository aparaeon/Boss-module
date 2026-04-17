package gg.mmorealms.module.pokemon.backend.neoforge.manager;

import com.google.gson.JsonObject;
import com.pixelmonmod.api.parsing.ParseAttempt;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.api.registry.RegistryValue;
import com.pixelmonmod.pixelmon.api.pokedex.PokeDexStorageProxy;
import com.pixelmonmod.pixelmon.api.pokedex.PokedexStorage;
import com.pixelmonmod.pixelmon.api.pokedex.ServerStoredPokedex;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.pixelmonmod.pixelmon.items.SpriteItem;
import com.pixelmonmod.pixelmon.items.helpers.ItemHelper;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.manager.PokemonPlatformImplementation;
import gg.mmorealms.module.pokemon.backend.neoforge.PokemonNeoForgeModule;
import gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation.PixelmonPC;
import gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation.PixelmonParty;
import gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation.PixelmonPokedex;
import gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation.PixelmonPokemon;
import lombok.SneakyThrows;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class PixelmonPlatformImplementation extends PokemonPlatformImplementation {
	private static final RegistryAccess REGISTRY_ACCESS = PokemonNeoForgeModule.instance().getRegistryAccess();

	@Override
	public IPokemon fromProperties(String propertiesString) {
		ParseAttempt<PokemonSpecification> parse = PokemonSpecificationProxy.create(propertiesString.split(" "));
		if (!parse.wasSuccess()) {
			return null;
		}

		Pokemon nativePokemon = parse.get().create();

		if (nativePokemon == null) {
			return null;
		}

		return new PixelmonPokemon(nativePokemon);
	}

	@Override
	public PixelmonPokemon getPokemonFromEntity(Entity entity) {
		if (!(entity instanceof PixelmonEntity pokemonEntity)) {
			return null;
		}

		Pokemon nativePokemon = pokemonEntity.getPokemon();
		return new PixelmonPokemon(nativePokemon);
	}

	@Override
	public PixelmonPokemon getPokemonFromItem(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof SpriteItem)) {
			return null;
		}

		//noinspection removal
		CompoundTag tag = ItemHelper.getTag(itemStack);
		String jsonString = tag.getString("json");

		if (jsonString.isEmpty()) {
			Logger.error("Failed to get Pixelmon Pokemon from ItemStack: JSON is empty.");
			return null;
		}

		JsonObject json = PokemonNeoForgeModule.instance().fromJson(jsonString, JsonObject.class);
		return deserializePokemon(json);
	}

	@Override
	public PixelmonPokedex createEmptyPokedex(UUID uuid) {
		PokedexStorage pokedexStorage = new ServerStoredPokedex(uuid);
		return new PixelmonPokedex(pokedexStorage);
	}

	@Override
	public IPokemon create(String speciesName, boolean shiny) {
		if (speciesName == null) {
			return null;
		}

		Optional<RegistryValue<Species>> speciesRegistryOptional = PixelmonSpecies.get(speciesName);

		if (speciesRegistryOptional.isEmpty()) {
			return null;
		}

		Optional<Species> value = speciesRegistryOptional.get().getValue();

		if (value.isEmpty()) {
			return null;
		}

		Species species = value.get();
		Pokemon pokemon = PokemonFactory.create(species);
		pokemon.setShiny(shiny);

		return new PixelmonPokemon(pokemon);
	}

	@SneakyThrows
	@Override
	public PixelmonParty getParty(UUID uuid) {
		PlayerPartyStorage nativeParty = StorageProxy.getParty(uuid).get();
		if (nativeParty == null) {
			return null;
		}
		return new PixelmonParty(nativeParty);
	}

	@SneakyThrows
	@Override
	public PixelmonPC getPC(ServerPlayer player) {
		PCStorage nativePC = StorageProxy.getPCForPlayer(player.getUUID()).get();
		if (nativePC == null) {
			return null;
		}
		return new PixelmonPC(nativePC);
	}


	@Override
	public PixelmonParty getParty(ServerPlayer player) {
		return getParty(player.getUUID());
	}

	@Override
	public PixelmonPokedex getPokedex(ServerPlayer player) {
		PokedexStorage pokedexStorage = PokeDexStorageProxy.getStorageNow(player);
		return new PixelmonPokedex(pokedexStorage);
	}

	@Override
	public PixelmonPokemon deserializePokemon(JsonObject json) {
		CompoundTag nbt = CodecUtils.deserialize(CompoundTag.CODEC, json, CodecUtils.CodecErrorProcessor.ofNull());

		Pokemon nativePokemon = Pokemon.fromNBT(nbt).toPokemon();
		nativePokemon.readFromNBT(nbt, REGISTRY_ACCESS);

		return new PixelmonPokemon(nativePokemon);
	}

	@SneakyThrows
	@Override
	public PixelmonParty deserializeParty(UUID owner, JsonObject json) {
		CompoundTag nbt = CodecUtils.deserialize(CompoundTag.CODEC, json, CodecUtils.CodecErrorProcessor.ofNull());

		PlayerPartyStorage nativeParty = new PlayerPartyStorage(owner, true).readFromNBT(nbt, REGISTRY_ACCESS).get();
		return new PixelmonParty(nativeParty);
	}

	@Override
	public PixelmonPC deserializePC(UUID owner, JsonObject json) {
		CompoundTag nbt = CodecUtils.deserialize(CompoundTag.CODEC, json, CodecUtils.CodecErrorProcessor.ofNull());

		PCStorage nativePC = new PCStorage(owner);
		nativePC.readFromNBT(nbt, REGISTRY_ACCESS);

		return new PixelmonPC(nativePC);
	}

	@Override
	public PixelmonPokedex deserializePokedex(UUID owner, JsonObject json) {
		CompoundTag nbt = CodecUtils.deserialize(CompoundTag.CODEC, json, CodecUtils.CodecErrorProcessor.ofNull());
		ServerStoredPokedex pokedex = new ServerStoredPokedex(owner);
		pokedex.load(nbt, REGISTRY_ACCESS);
		return new PixelmonPokedex(pokedex);
	}

	@Override
	public Class<?> getNativePokemonClass() {
		return Pokemon.class;
	}

	@Override
	public Class<?> getNativePokedexClass() {
		return PokedexStorage.class;
	}

	@Override
	public Class<?> getNativePartyClass() {
		return PlayerPartyStorage.class;
	}

	@Override
	public Class<?> getNativePCClass() {
		return PCStorage.class;
	}

	@Override
	public Class<? extends Entity> getNativePokemonEntityClass() {
		return PixelmonEntity.class;
	}

}
