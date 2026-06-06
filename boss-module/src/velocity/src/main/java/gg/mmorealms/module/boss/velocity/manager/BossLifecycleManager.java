package gg.mmorealms.module.boss.velocity.manager;

import gg.mmorealms.module.core.common.utils.AliasTable;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.analytics.velocity.AnalyticsVelocityModule;
import gg.mmorealms.module.analytics.velocity.manager.AfkManager;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.boss.common.event.BossSpawnEvent;
import gg.mmorealms.module.boss.velocity.config.BossVelocityConfig;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BossLifecycleManager {

	private final BossVelocityConfig config;
	private final ServerManager serverManager;
	private @Nullable CancelableTimeTask spawnTask;

	public BossLifecycleManager(BossVelocityConfig config, ServerManager serverManager) {
		this.config = config;
		this.serverManager = serverManager;
		buildTierSampler();
	}

	private void buildTierSampler() {
		AliasTable<BossTier> sampler = new AliasTable<>();
		for (var entry : config.tierWeights.entrySet()) {
			int weight = entry.getValue();
			if (weight <= 0) {
				Logger.info("Tier " + entry.getKey() + " has no weight, will never spawn.");
				continue;
			}
			sampler.add(entry.getKey(), weight);
		}
		sampler.build();
		this.config.tierSampler = sampler;
	}

	public synchronized void start() {
		if (spawnTask != null && !spawnTask.isCanceled()) {
			return;
		}
		Logger.info("Starting boss spawn coordinator (interval: " + config.spawnInterval + ")");
		spawnTask = ScheduleUtils.runTaskTimer(this::tick, config.spawnInterval);
	}

	public synchronized void stop() {
		if (spawnTask != null) {
			spawnTask.cancel();
			spawnTask = null;
		}
	}

	private void tick() {
		// Roll spawn chance.
		AfkManager afk = AnalyticsVelocityModule.instance().getAfkManager();
		int activeNonAfk = countAllNonAfk(afk);
		double effective = config.baseSpawnChance + (activeNonAfk * config.playerSpawnChanceBias);
		if (ThreadLocalRandom.current().nextDouble(100.0) >= effective) {
			return;
		}

		// Pick AFK-aware target backend.
		EngineServer target = pickTargetServer(afk);
		if (target == null) {
			Logger.debug("BossScheduler: no eligible WILD backend; skipping tick.");
			return;
		}

		BossTier tier = config.tierSampler.next();
		if (tier == null) {
			Logger.warn("BossScheduler: tier sampler returned null; check tierWeights.");
			return;
		}

		List<UUID> eligible = buildEligibleUUIDs(target, afk);
		if (eligible.isEmpty()) {
			Logger.debug("BossScheduler: no eligible non-AFK players on chosen backend; skipping tick.");
			return;
		}

		new BossSpawnEvent(
				target.getServerID(),
				tier,
				null,
				false,
				null,
				null,
				eligible
		).send();
	}

	private int countAllNonAfk(AfkManager afk) {
		int total = 0;
		for (EngineServer es : serverManager.getServers(ServerType.WILD)) {
			RegisteredServer rs = es.getProxyServer();
			if (rs == null) continue;
			for (Player p : rs.getPlayersConnected()) {
				if (!afk.isAfk(p.getUniqueId())) total++;
			}
		}
		return total;
	}

	public @Nullable EngineServer pickTargetServer(AfkManager afk) {
		List<EngineServer> candidates = serverManager.getServers(ServerType.WILD).stream()
				.filter(es -> {
					RegisteredServer rs = es.getProxyServer();
					if (rs == null) return false;
					return rs.getPlayersConnected().stream()
							.anyMatch(p -> !afk.isAfk(p.getUniqueId()));
				})
				.collect(Collectors.toList());
		if (candidates.isEmpty()) return null;
		return candidates.stream()
				.min(Comparator.comparingInt(es -> {
					RegisteredServer rs = es.getProxyServer();
					return rs == null ? Integer.MAX_VALUE : rs.getPlayersConnected().size();
				}))
				.orElse(null);
	}

	public List<UUID> buildEligibleUUIDs(EngineServer target, AfkManager afk) {
		RegisteredServer rs = target.getProxyServer();
		if (rs == null) return List.of();
		return rs.getPlayersConnected().stream()
				.map(Player::getUniqueId)
				.filter(uuid -> !afk.isAfk(uuid))
				.collect(Collectors.toList());
	}

}
