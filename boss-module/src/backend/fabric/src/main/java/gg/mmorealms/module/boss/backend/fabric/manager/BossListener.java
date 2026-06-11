package gg.mmorealms.module.boss.backend.fabric.manager;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.common.event.BossSpawnEvent;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.BattleWonEvent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Auto-scanned listener for MMO @EventHandler events. Fabric/Cobblemon-native callbacks live in BossFabricModule.
 * BattleWonEvent fires off-main (fireAsync) → wrap entity/world mutations in runOnMain.
 */
@OnlyOn(servers = ServerType.WILD)
public class BossListener {

	public BossListener() {
	}

	@EventHandler
	public void onBossSpawn(BossSpawnEvent ev) {
		BossFabricModule mod = BossFabricModule.instance();
		BossSpawner spawner = mod != null ? mod.getBossSpawner() : null;
		if (spawner != null) {
			spawner.handleSpawnRequest(ev); // already hops to main internally
		}
	}

	@EventHandler
	public void onBattleWon(BattleWonEvent ev) {
		BossFabricModule mod = BossFabricModule.instance();
		if (mod == null) return;
		BossManager manager = mod.getBossManager();
		if (manager == null) return;

		// Locate any boss UUID among the losers. Wild PvE battles have a single PokemonBattleActor
		// on the losing side, but iterate the full list to be defensive.
		UUID defeatedBossUuid = findBossUuidIn(ev.getLosers());

		if (defeatedBossUuid != null) {
			UUID winnerUuid = findFirstPlayerUuidIn(ev.getWinners());
			Logger.debug("BattleWon: detected boss defeat uuid=" + defeatedBossUuid
					+ " winner=" + winnerUuid);
			if (winnerUuid != null) {
				// fireAsync → off main thread. Hop to main for entity/world/command work.
				mod.runOnMain(() -> {
					ServerPlayer winner = mod.getServer() != null
							? mod.getServer().getPlayerList().getPlayer(winnerUuid) : null;
					if (winner == null) {
						manager.handleDefeatWithoutReward(defeatedBossUuid, winnerUuid,
								"winner " + winnerUuid + " disconnected before reward dispatch");
						return;
					}
					manager.handleDefeat(defeatedBossUuid, winner);
				});
			} else {
				// Battle was won but no PlayerBattleActor on the winning side (e.g. AI-only).
				mod.runOnMain(() -> manager.handleDefeatWithoutReward(defeatedBossUuid, null,
						"no player on winning side"));
			}
		}

		// Only sweep pending despawns when there's actually work to do.
		if (defeatedBossUuid != null || manager.hasPendingDespawns()) {
			mod.runOnMain(manager::retryPendingDespawns);
		}
	}

	/** Match by {@code originalPokemon.uuid} — effectedPokemon may be a battle-clone with a different UUID. */
	private @Nullable UUID findBossUuidIn(@Nullable List<BattleActor> actors) {
		if (actors == null) return null;
		BossManager manager = BossFabricModule.instance().getBossManager();
		if (manager == null) return null;
		for (BattleActor actor : actors) {
			if (!(actor instanceof PokemonBattleActor pokeActor)) continue;
			UUID uuid = pokeActor.getPokemon().getOriginalPokemon().getUuid();
			if (manager.get(uuid) != null) {
				return uuid;
			}
		}
		return null;
	}

	private @Nullable UUID findFirstPlayerUuidIn(@Nullable List<BattleActor> actors) {
		if (actors == null) return null;
		for (BattleActor actor : actors) {
			if (actor instanceof PlayerBattleActor playerActor) {
				return playerActor.getUuid();
			}
		}
		return null;
	}
}
