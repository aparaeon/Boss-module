package gg.mmorealms.module.analytics.velocity.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AfkManager {

	@SuppressWarnings({"FieldCanBeLocal", "unused"}) // GC prevention
	private final CompletableFuture<Void> afkPlayerProcessorTask;
	private final HashMap<UUID, Movement> userMovements = new HashMap<>();

	public AfkManager() {
		this.afkPlayerProcessorTask = ScheduleUtils.runTaskAsync(() -> {
			long processingInterval = Time.minutes(1).toMilliseconds();
			long lastProcessingTime = System.currentTimeMillis();

			while (true) {
				this.processAfkPlayers(lastProcessingTime);
				lastProcessingTime = System.currentTimeMillis();
				try {
					Thread.sleep(processingInterval);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
	}

	public void recordMovement(UUID uuid, double x, double y, double z) {
		Movement movement = userMovements.computeIfAbsent(uuid, k -> new Movement());

		if (movement.lastX == x && movement.lastY == y && movement.lastZ == z) {
			return;
		}

		movement.lastMovementTimestamp = System.currentTimeMillis();

		movement.lastX = x;
		movement.lastY = y;
		movement.lastZ = z;
	}

	public void recordMouseMovement(UUID uuid, float yaw, float pitch) {
		Movement movement = userMovements.computeIfAbsent(uuid, k -> new Movement());

		if (movement.lastYaw == yaw && movement.lastPitch == pitch) {
			return;
		}

		movement.lastMouseMovementTimestamp = System.currentTimeMillis();

		movement.lastYaw = yaw;
		movement.lastPitch = pitch;
	}

	public List<UUID> getAFKPlayers() {
		List<UUID> result = new ArrayList<>();

		for (UUID uuid : this.userMovements.keySet()) {
			if (isAfk(uuid)) {
				result.add(uuid);
			}
		}

		return result;
	}

	private void processAfkPlayers(long lastProcessingTime) {
		long currentTime = System.currentTimeMillis();
		long interval = currentTime - lastProcessingTime;

		for (UUID afkPlayer : getAFKPlayers()) {
			UserStats.getByUUID(afkPlayer).recordAfkTime(interval);
		}
	}

	public boolean isAfk(UUID uuid) {
		Movement movement = this.userMovements.get(uuid);

		if (movement == null) {
			return false;
		}

		long currentTime = System.currentTimeMillis();
		long afkThreshold = Time.minutes(1).toMilliseconds();

		return currentTime - movement.lastMovementTimestamp > afkThreshold ||
				currentTime - movement.lastMouseMovementTimestamp > afkThreshold;
	}

	private static class Movement {
		private long lastMovementTimestamp = 0;

		private double lastX = 0;
		private double lastY = 0;
		private double lastZ = 0;

		private long lastMouseMovementTimestamp = 0;

		private float lastYaw = 0;
		private float lastPitch = 0;
	}

	public void onLeave(UUID uuid) {
		this.userMovements.remove(uuid);
	}
}
