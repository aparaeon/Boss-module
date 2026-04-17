package gg.mmorealms.module.legendaries.velocity.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import gg.mmorealms.module.legendaries.common.dto.event.LegendaryDespawnEvent;
import gg.mmorealms.module.legendaries.common.dto.event.LegendarySpawnEvent;
import gg.mmorealms.module.legendaries.common.dto.request.LegendaryHeartbeatRequest;
import gg.mmorealms.module.legendaries.velocity.LegendariesVelocityModule;
import gg.mmorealms.module.legendaries.velocity.config.LegendarySpawnConfig;
import gg.mmorealms.module.legendaries.velocity.utils.NetworkUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class LegendaryLifecycleManager {
	private final LegendarySpawnConfig config;
	private final LegendaryInfoManager infoManager;

	private CancelableTimeTask spawnTask;
	private CancelableTimeTask heartbeatTask;
	private final Map<UUID, CancelableTimeTask> despawnTasks = new ConcurrentHashMap<>();

	public LegendaryLifecycleManager(LegendaryInfoManager infoManager) {
		this.config = LegendariesVelocityModule.instance().getConfig();
		this.infoManager = infoManager;
	}

	public synchronized void start() {
		if (isTaskActive(spawnTask)) {
			return;
		}
		Logger.info("Starting legendary spawn coordinator");

		spawnTask = ScheduleUtils.runTaskTimer(
				this::attemptSpawnCycle,
				config.spawnAttemptCooldown);

		if (isTaskActive(heartbeatTask)) {
			return;
		}
		heartbeatTask = ScheduleUtils.runTaskTimer(
				this::sendHeartbeat,
				Time.seconds(20));
	}

	private boolean isTaskActive(CancelableTimeTask task) {
		return task != null && !task.isCanceled();
	}

	/* ---------- Spawn ---------- */

	private void attemptSpawnCycle() {
		if (!shouldSpawn()) {
			return;
		}

		sendSpawnRequest(config.sendSpawnRequestToEmptyServer);
	}

	public boolean sendSpawnRequest(boolean canSendOnEmtpy) {
		return sendSpawnRequest(canSendOnEmtpy, null);
	}

	public boolean sendSpawnRequest(boolean canSendOnEmtpy, UUID senderUUID) {
		int spawnAmount = calculateSpawnAmount();
		return sendSpawnRequest(canSendOnEmtpy, spawnAmount, senderUUID);
	}

	public boolean sendSpawnRequest(boolean canSendOnEmtpy, int spawnAmount, @Nullable UUID senderUUID) {
		for (int i = 0; i < spawnAmount; i++) {
			// Choose random wild
			EngineServer targetServer = NetworkUtils.getRandomServer(ServerType.WILD, canSendOnEmtpy);
			if (targetServer == null) {
				return false;
			}

			String targetServerId = targetServer.getServerID();
			// Send spawn request
			Logger.debug("Attempting to spawn legendary on server: " + targetServerId);
			new LegendarySpawnEvent(targetServerId, senderUUID).send();
		}

		return true;
	}

	private boolean shouldSpawn() {
		float playerCount = NetworkUtils.getPlayerCount();
		float spawnChance = config.baseSpawnChance + playerCount * config.playerSpawnChanceBias;

		return ThreadLocalRandom.current().nextFloat(100) < spawnChance;
	}

	private int calculateSpawnAmount() {
		return Math.round(1 + NetworkUtils.getPlayerCount() * config.playerSpawnMultiplierBias);
	}

	/* ---------- Despawn ---------- */

	public void scheduleDespawn(UUID pokemonUUID) {
		scheduleDespawn(pokemonUUID, System.currentTimeMillis());
	}

	public void scheduleDespawn(UUID pokemonUUID, long spawnTime) {
		Time despawnTime = calculateDespawnTime(spawnTime);
		Logger.debug("Scheduling despawn in " + despawnTime.toSeconds() + " seconds.");

		CancelableTimeTask task = ScheduleUtils.runTaskLater(() -> sendDespawnRequest(pokemonUUID), despawnTime);
		despawnTasks.putIfAbsent(pokemonUUID, task);
	}

	private Time calculateDespawnTime(long spawnTime) {
		return Time.milliseconds(Math.max(1000,
				(spawnTime + config.despawnTime.toMilliseconds() - System.currentTimeMillis())));
	}

	public void clearDespawnTask(UUID pokemonUUID) {
		CancelableTimeTask task = despawnTasks.get(pokemonUUID);
		if (isTaskActive(task)) {
			task.cancel();
			despawnTasks.remove(pokemonUUID);
		}
	}

	public boolean sendDespawnRequest(UUID pokemonUUID) {
		LegendaryInfo info = infoManager.get(pokemonUUID);
		if (info == null) {
			return false;
		}

		String targetServerId = info.getServerID();
		Logger.debug("Attempting to despawn legendary on server: " + targetServerId);

		if (targetServerId == null) {
			Logger.warn("Can't send despawn request, targetServerId is null.");
			infoManager.remove(pokemonUUID);
			return false;
		}

		new LegendaryDespawnEvent(targetServerId, pokemonUUID).send();

		return true;
	}

	private void sendHeartbeat() {
		Logger.debug("Sending legendary heartbeat...");

		Collection<LegendaryInfo> info = infoManager.getAll();

		if (info.isEmpty()) {
			Logger.debug("Can't send heartbeat, LegendaryInfo is empty");
			return;
		}

		for (LegendaryInfo legendaryInfo : info) {
			String serverId = legendaryInfo.getServerID();
			UUID pokemonUUID = legendaryInfo.getPokemonUUID();

			if (serverId == null) {
				Logger.debug("Can't send heartbeat, targetServerId is null");
				infoManager.remove(pokemonUUID);
				return;
			}

			LegendaryHeartbeatRequest.Response response = LegendaryHeartbeatRequest.to(serverId).sendAndGet();

			if (response == null) {
				Logger.warn("Failed to get heartbeat response from wild: " + serverId);
				infoManager.remove(pokemonUUID);
				return;
			}

			// If proxy crashed / restarted while wild is active with spawned legendary
			// We need to reschedule despawn
			handleDespawnReschedule(response);
		}
	}

	private void handleDespawnReschedule(LegendaryHeartbeatRequest.Response response) {
		response.getInfo().forEach(info -> {
			UUID uuid = info.getPokemonUUID();
			long spawnTime = info.getSpawnTime();

			CancelableTimeTask task = despawnTasks.get(uuid);

			if (!isTaskActive(task)) {
				scheduleDespawn(uuid, spawnTime);
			}
		});
	}

}