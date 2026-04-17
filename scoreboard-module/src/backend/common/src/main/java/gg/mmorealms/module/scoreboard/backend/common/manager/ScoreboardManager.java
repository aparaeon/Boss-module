package gg.mmorealms.module.scoreboard.backend.common.manager;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.scoreboard.backend.common.ScoreboardBackendModule;
import gg.mmorealms.module.scoreboard.backend.common.dto.ScoreboardObjective;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

import java.util.*;

public class ScoreboardManager {

	private static final int UPDATE_INTERVAL = 20;

	private static final int DISPLAY_DELAY_TICKS = 40;

	private int tickCounter = 0;

	private final Map<UUID, ScoreboardObjective> objectivesMap = new HashMap<>();
	private final Map<UUID, Integer> pendingDisplay = new HashMap<>();

	public ScoreboardManager() {
	}

	public void onJoin(ServerPlayer player) {
		UUID uuid = player.getUUID();

		ScoreboardObjective objective = new ScoreboardObjective(uuid);

		Scoreboard scoreboard = player.server.getScoreboard();
		Objective staleObjective = scoreboard.getObjective(objective.getName());
		if (staleObjective != null) {
			scoreboard.removeObjective(staleObjective);
		}

		objective.toNativeObjective();
		objectivesMap.put(uuid, objective);
		objective.update();

		pendingDisplay.put(uuid, DISPLAY_DELAY_TICKS);
	}

	public void onLeave(ServerPlayer player) {
		UUID uuid = player.getUUID();
		pendingDisplay.remove(uuid);

		ScoreboardObjective objective = objectivesMap.remove(uuid);
		if (objective == null) {
			return;
		}
		objective.destroy();
	}

	public void onTick() {
		Iterator<Map.Entry<UUID, Integer>> it = pendingDisplay.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Integer> entry = it.next();
			int remaining = entry.getValue() - 1;
			if (remaining <= 0) {
				it.remove();
				pushScoreboardToClient(entry.getKey());
			} else {
				entry.setValue(remaining);
			}
		}

		if (tickCounter++ < UPDATE_INTERVAL) {
			return;
		}
		tickCounter = 0;

		for (ServerPlayer player : ScoreboardBackendModule.instance().getServer().getPlayerList().getPlayers()) {
			ScoreboardObjective objective = objectivesMap.get(player.getUUID());
			if (objective == null) {
				continue;
			}
			objective.update();
		}
	}

	private void pushScoreboardToClient(UUID uuid) {
		ScoreboardObjective objective = objectivesMap.get(uuid);
		if (objective == null) {
			return;
		}

		ServerPlayer player = ScoreboardBackendModule.instance()
			.getServer().getPlayerList().getPlayer(uuid);
		if (player == null) {
			return;
		}

		Objective nativeObjective = objective.toNativeObjective();

		Logger.debug("Pushing full scoreboard to client for " + uuid);

		player.connection.send(new ClientboundSetObjectivePacket(nativeObjective, 0));
		objective.update();
		player.connection.send(new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, nativeObjective));
	}
}