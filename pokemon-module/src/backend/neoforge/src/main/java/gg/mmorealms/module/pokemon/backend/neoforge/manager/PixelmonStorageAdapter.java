package gg.mmorealms.module.pokemon.backend.neoforge.manager;

import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.PokemonStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageSaveAdapter;
import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.loader.common.dto.database.cache.AutoSaveCache;
import gg.mmorealms.module.pokemon.backend.common.dto.database.PokemonData;
import gg.mmorealms.module.pokemon.backend.neoforge.PokemonNeoForgeModule;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PixelmonStorageAdapter implements StorageSaveAdapter {

	private final StorageSaveAdapter fallbackSaveAdapter;

	// Optimization trick as pc and party data are not requested in one go, so we cache the pokemon data for a short time
	private final AutoSaveCache<UUID, PokemonData> cache;

	public PixelmonStorageAdapter(StorageSaveAdapter fallbackSaveAdapter) {
		this.fallbackSaveAdapter = fallbackSaveAdapter;
		this.cache = new AutoSaveCache<>(UUID.class, PokemonData.class, Time.seconds(5), (key, value) -> true);
		this.cache.disableLog();
	}

	@Override
	public void save(PokemonStorage pokemonStorage, HolderLookup.Provider provider) {
		if (pokemonStorage instanceof PlayerPartyStorage || pokemonStorage instanceof PCStorage) {
			return;
		}

		fallbackSaveAdapter.save(pokemonStorage, provider);
	}

	private PokemonData getPokemonData(UUID uuid) {
		PokemonData pokemonData = cache.get(uuid);

		if (pokemonData != null) {
			return pokemonData;
		}

		pokemonData = PokemonData.get(uuid);

		if (pokemonData == null) {
			return null;
		}

		return cache.put(uuid, pokemonData);
	}

	@Override
	public @NotNull <T extends PokemonStorage> CompletableFuture<T> load(UUID uuid, Class<T> clazz, HolderLookup.Provider provider) {
		if (clazz.equals(PlayerPartyStorage.class)) {
			PokemonData pokemonData = getPokemonData(uuid);

			if (pokemonData == null) {
				return fallbackSaveAdapter.load(uuid, clazz, provider);
			}

			return (CompletableFuture<T>) CompletableFuture.completedFuture(PokemonNeoForgeModule.instance().getPlatformImplementation().deserializeParty(uuid, pokemonData.getPartyData()).getNative());
		}

		if (clazz.equals(PCStorage.class)) {
			PokemonData pokemonData = getPokemonData(uuid);

			if (pokemonData == null) {
				return fallbackSaveAdapter.load(uuid, clazz, provider);
			}

			return (CompletableFuture<T>) CompletableFuture.completedFuture(PokemonNeoForgeModule.instance().getPlatformImplementation().deserializePC(uuid, pokemonData.getPcData()).getNative());
		}

		return fallbackSaveAdapter.load(uuid, clazz, provider);
	}
}
