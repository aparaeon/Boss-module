package gg.mmorealms.module.pokemon.backend.fabric.manager;

import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataFactory;
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes;
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import gg.mmorealms.module.pokemon.backend.common.dto.database.PokedexData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class PokedexDataFactory implements PlayerInstancedDataFactory<PokedexManager> {

	private final HashMap<UUID, PokedexManager> pokedexCache = new HashMap<>();

	@Override
	public @NotNull PokedexManager getForPlayer(@NotNull Player player) {
		return getForPlayer(player.getUUID());
	}

	@Override
	public void setup(@NotNull MinecraftServer minecraftServer) {
	}

	@Override
	public @NotNull PokedexManager getForPlayer(@NotNull UUID uuid) {
		if (pokedexCache.containsKey(uuid)) {
			return pokedexCache.get(uuid);
		}
		PokedexManager pokedexManager = (PokedexManager) PokedexData.get(uuid).deserialize().getNative();
		pokedexCache.put(uuid, pokedexManager);
		return pokedexManager;
	}

	@Override
	public void saveAll() {
	}

	@Override
	public void saveSingle(@NotNull Player player) {
	}

	@Override
	public void saveSingle(@NotNull UUID uuid) {
	}

	@Override
	public void onPlayerDisconnect(@NotNull ServerPlayer serverPlayer) {
		ScheduleUtils.runTaskLater(
				() -> pokedexCache.remove(serverPlayer.getUUID()),
				Time.seconds(5)
		);
	}

	@Override
	public void sendToPlayer(@NotNull ServerPlayer serverPlayer) {
		new SetClientPlayerDataPacket(
				PlayerInstancedDataStoreTypes.INSTANCE.getPOKEDEX(),
				getForPlayer(serverPlayer).toClientData(),
				false
		).sendToPlayer(serverPlayer);
	}


}
