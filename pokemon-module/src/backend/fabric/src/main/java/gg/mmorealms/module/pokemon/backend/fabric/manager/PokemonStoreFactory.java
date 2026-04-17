package gg.mmorealms.module.pokemon.backend.fabric.manager;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.PokemonStore;
import com.cobblemon.mod.common.api.storage.StorePosition;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.block.entity.PCBlockEntity;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.pokemon.backend.common.dto.database.PokemonData;
import gg.mmorealms.module.pokemon.backend.fabric.PokemonFabricModule;
import kotlin.Unit;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PokemonStoreFactory implements com.cobblemon.mod.common.api.storage.factory.PokemonStoreFactory {

	private static final CobblemonPlatformImplementation PLATFORM_IMPLEMENTATION = PokemonFabricModule.instance().getPlatformImplementation();
	private static final Map<Class<? extends PokemonStore<?>>, StoreCache<?, ?>> STORE_CACHES = new ConcurrentHashMap<>();

	public static void removeFromCache(UUID uuid) {
		for (Map.Entry<Class<? extends PokemonStore<?>>, StoreCache<?, ?>> entry : STORE_CACHES.entrySet()) {
			StoreCache<?, ?> cache = entry.getValue();
			Logger.debug(new MessageBuilder("Removing cache of type {type} for {uuid}")
					.parse("type", entry.getKey().getSimpleName())
					.parse("uuid", uuid)
					.parse());
			cache.cacheMap.remove(uuid);
		}
	}

	protected <E extends StorePosition, T extends PokemonStore<E>> StoreCache<E, T> getStoreCache(Class<T> storeClass) {
		//noinspection unchecked
		return (StoreCache<E, T>) STORE_CACHES.computeIfAbsent(storeClass, key -> new StoreCache<E, T>());
	}

	public <E extends StorePosition, T extends PokemonStore<E>> @Nullable T getStore(@NotNull Class<T> clazz, @NotNull UUID uuid, Function<UUID, T> constructor) {
		Map<UUID, T> cache = this.getStoreCache(clazz).getCacheMap();
		T cached = cache.get(uuid);

		if (cached != null) {
			return cached;
		}

		T store;
		if (constructor != null) {
			store = constructor.apply(uuid);
		} else {
			try {
				store = clazz.getConstructor(UUID.class).newInstance(uuid);
			} catch (Exception error) {
				Logger.error(error);
				return null;
			}
		}

		if (store != null) {
			store.initialize();
			cache.put(uuid, store);
			return store;
		}

		Logger.error("Failed to get store for " + uuid + " of type " + clazz.getName());
		return null;
	}

	private PlayerPartyStore constructParty(UUID uuid) {
		PokemonData data = PokemonData.get(uuid);

		if (data == null) {
			Logger.debug("No party data found for " + uuid);
			return new PlayerPartyStore(uuid, uuid);
		}

		Logger.debug("Deserializing party data for " + uuid);
		return PLATFORM_IMPLEMENTATION.deserializeParty(uuid, data.getPartyData()).getNative();
	}

	private PCStore constructPC(UUID uuid) {
		PokemonData data = PokemonData.get(uuid);

		if (data == null) {
			Logger.debug("No PC data found for " + uuid);

			PCStore pcStore = new PCStore(uuid);
			pcStore.resize(Cobblemon.config.getDefaultBoxCount(), false, (pokemon) -> {
				pcStore.relocateEvictedBoxPokemon(pokemon);
				return Unit.INSTANCE;
			});
			return pcStore;
		}

		Logger.debug("Deserializing PC data for " + uuid);
		return PLATFORM_IMPLEMENTATION.deserializePC(uuid, data.getPcData()).getNative();
	}

	@Override
	public @Nullable PlayerPartyStore getPlayerParty(@NotNull UUID uuid, @NotNull RegistryAccess registryAccess) {
		return getStore(PlayerPartyStore.class, uuid, this::constructParty);
	}

	@Override
	public @Nullable PCStore getPC(@NotNull UUID uuid, @NotNull RegistryAccess registryAccess) {
		return getStore(PCStore.class, uuid, this::constructPC);
	}

	@Override
	public @Nullable PCStore getPCForPlayer(@NotNull ServerPlayer serverPlayer, @NotNull PCBlockEntity pcBlockEntity) {
		Level level = pcBlockEntity.getLevel();

		if (level == null) {
			return null;
		}

		return getPC(serverPlayer.getUUID(), level.registryAccess());
	}

	@Override
	public @Nullable <E extends StorePosition, T extends PokemonStore<E>> T getCustomStore(@NotNull Class<T> clazz, @NotNull UUID uuid, @NotNull RegistryAccess registryAccess) {
		return this.getStore(clazz, uuid, null);
	}

	@Override
	public void shutdown(@NotNull RegistryAccess registryAccess) {

	}

	@Override
	public void onPlayerDisconnect(@NotNull ServerPlayer player) {
		// Something is accessing the PC data of the user on disconnect after this event. We do not have any other way
		// to order this so we delay it by 5 seconds. This should be enough, but if it is causing issues in the future
		// this value can be changed
		ScheduleUtils.runTaskLater(() ->
						PokemonStoreFactory.removeFromCache(player.getUUID()),
				Time.seconds(5)
		);
	}

	@Getter
	protected static class StoreCache<E extends StorePosition, T extends PokemonStore<E>> {
		private final Map<UUID, T> cacheMap = new ConcurrentHashMap<>();
	}
}