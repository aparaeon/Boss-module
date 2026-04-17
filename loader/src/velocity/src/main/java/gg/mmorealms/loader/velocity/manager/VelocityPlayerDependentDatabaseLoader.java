package gg.mmorealms.loader.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.loader.velocity.VelocityLoader;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

public abstract class VelocityPlayerDependentDatabaseLoader<LoadedObject extends ISavable>
		extends DatabaseLoader<UUID, LoadedObject, LoadedObject> {

	private static final HashSet<UUID> LOADER_PLAYER_UUIDs = new HashSet<>();

	public VelocityPlayerDependentDatabaseLoader(Class<LoadedObject> loadedObjectClass) {
		super(loadedObjectClass);
	}

	public static void joined(@NotNull Player player) {
		if (LOADER_PLAYER_UUIDs.contains(player.getUniqueId())) {
			return;
		}

		for (DatabaseLoader<?, ?, ?> _loader : ALL) {
			if (!(_loader instanceof VelocityPlayerDependentDatabaseLoader<?> loader)) {
				continue;
			}
			loader.onJoin(player);
		}

		LOADER_PLAYER_UUIDs.add(player.getUniqueId());
	}

	public static void left(@NotNull Player player) {
		if (!LOADER_PLAYER_UUIDs.contains(player.getUniqueId())) {
			return;
		}

		for (DatabaseLoader<?, ?, ?> _loader : ALL) {
			if (!(_loader instanceof VelocityPlayerDependentDatabaseLoader<?> loader)) {
				continue;
			}
			loader.onLeave(player);
			loader.clearCache(player.getUniqueId(), true);
		}

		LOADER_PLAYER_UUIDs.remove(player.getUniqueId());
	}

	@Override
	protected boolean shouldClearCache(@NotNull UUID uuid, @NotNull LoadedObject loadedObject) {
		return VelocityLoader.instance().getProxy().getPlayer(uuid).isEmpty();
	}

	public abstract void onJoin(@NotNull Player player);

	public abstract void onLeave(@NotNull Player player);
}
