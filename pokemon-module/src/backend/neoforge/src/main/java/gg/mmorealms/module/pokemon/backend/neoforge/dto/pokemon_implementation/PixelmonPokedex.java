package gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation;

import com.google.gson.JsonObject;
import com.pixelmonmod.pixelmon.api.pokedex.Pokedex;
import com.pixelmonmod.pixelmon.api.pokedex.PokedexStorage;
import com.pixelmonmod.pixelmon.api.pokedex.StoredPokedex;
import com.pixelmonmod.pixelmon.api.util.helpers.RegistryHelper;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokedex;
import gg.mmorealms.module.pokemon.backend.neoforge.PokemonNeoForgeModule;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Getter
public class PixelmonPokedex implements IPokedex {

	private static Field STORED_POKEDEX___POKEDEXES_FIELD;

	static {
		try {
			STORED_POKEDEX___POKEDEXES_FIELD = StoredPokedex.class.getField("pokedexes");
			STORED_POKEDEX___POKEDEXES_FIELD.setAccessible(true);
		} catch (NoSuchFieldException exception) {
			Logger.error(exception);
		}
	}

	private final PokedexStorage nativePokedex;

	public PixelmonPokedex(PokedexStorage nativePokedex) {
		this.nativePokedex = nativePokedex;
	}

	@Override
	public PokedexStorage getNative() {
		return nativePokedex;
	}

	@Override
	public JsonObject serialize() {
		CompoundTag nbt = this.nativePokedex.save(PokemonNeoForgeModule.instance().getRegistryAccess());
		return CodecUtils.serialize(CompoundTag.CODEC, nbt);
	}

	@Override
	public int getMaxSize() {
		int total = 0;
		Registry<Pokedex> registry = RegistryHelper.registryAccess().registryOrThrow(Pokedex.REGISTRY);

		for (Map.Entry<ResourceKey<Pokedex>, PokedexStorage> entry : this.getPokedexes().entrySet()) {
			Pokedex pokedex = registry.get(entry.getKey());

			if (pokedex == null) {
				Logger.warn("Failed to get pokedex for key: " + entry.getKey().location());
				continue;
			}

			total += pokedex.pokemon().getPokemon().size();
		}

		return total;
	}

	public List<Holder.Reference<Pokedex>> getAvailablePokedexes() {
		RegistryAccess registryAccess = this.nativePokedex.getOwner().registryAccess();
		Registry<Pokedex> registry = registryAccess.registryOrThrow(Pokedex.REGISTRY);
		return registry.holders().toList();
	}


	@Override
	public int getSize() {
		int total = 0;

		for (Map.Entry<ResourceKey<Pokedex>, PokedexStorage> entry : this.getPokedexes().entrySet()) {
			total += entry.getValue().countCaught();
		}


		return total;
	}

	@SneakyThrows
	public Map<ResourceKey<Pokedex>, PokedexStorage> getPokedexes() {
		//noinspection unchecked
		return (Map<ResourceKey<Pokedex>, PokedexStorage>) STORED_POKEDEX___POKEDEXES_FIELD.get(this.nativePokedex);
	}

}
