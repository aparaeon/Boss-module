package gg.mmorealms.module.realms.velocity.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.realms.common.dto.RealmState;
import gg.mmorealms.module.realms.velocity.RealmsVelocityModule;
import gg.mmorealms.module.realms.velocity.config.RealmsConfig;
import gg.mmorealms.module.realms.velocity.dto.ProxyRealm;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RealmsManager {

	private final static Time CHECK_FOR_UNRESPONSIVE_REALMS_INTERVAL = Time.minutes(5);

	private final CancelableTimeTask unresponsiveRealmsCleanerTask;

	@Getter
	private final Map<UUID, ProxyRealm> realmMap = new ConcurrentHashMap<>();

	public RealmsManager() {
		unresponsiveRealmsCleanerTask = ScheduleUtils.runTaskTimer(() -> {
			RealmsConfig config = RealmsVelocityModule.instance().getConfig();
			List<UUID> removeList = new ArrayList<>();
			for (Map.Entry<UUID, ProxyRealm> entry : realmMap.entrySet()) {
				Time timeout = config.realmStateTimeouts.get(entry.getValue().getState());
				if (timeout.toMilliseconds() == 0L) {
					continue;
				}

				if (entry.getValue().getStateChangeTimestamp() + timeout.toMilliseconds() <= System.currentTimeMillis()) {
					Logger.info("Found unresponsive realm " + entry.getKey() + " in " + entry.getValue().getState() + " state");
					removeList.add(entry.getKey());
				}
			}

			for (UUID remove : removeList) {
				realmMap.remove(remove);
			}
		}, CHECK_FOR_UNRESPONSIVE_REALMS_INTERVAL);
	}

	public void unregisterAll(String serverID) {
		List<UUID> toRemove = new ArrayList<>();

		for (Map.Entry<UUID, ProxyRealm> realmEntry : realmMap.entrySet()) {
			ProxyRealm proxyRealm = realmEntry.getValue();

			if (proxyRealm.getServerID().equals(serverID)) {
				toRemove.add(realmEntry.getKey());
			}
		}

		toRemove.forEach(realmMap::remove);
	}

	public void setRealmState(@NotNull String actionOriginatorServer, UUID ownerUUID, RealmState newState) {
		ProxyRealm proxyRealm = realmMap.get(ownerUUID);

		if (proxyRealm == null) {
			if (newState == null) {
				return;
			}

			if (newState != RealmState.LOADING) {
				Logger.error(new MessageBuilder("Server {server} attempted to update state of non-existing realm entry {owner_uuid} to {state}")
						.parse("owner_uuid", ownerUUID)
						.parse("server", actionOriginatorServer)
						.parse("state", newState)
				);
				return;
			}

			realmMap.put(ownerUUID, new ProxyRealm(actionOriginatorServer, ownerUUID));
			return;
		}

		String serverID = proxyRealm.getServerID();

		if (!actionOriginatorServer.equals(serverID) && !actionOriginatorServer.equals("proxy")) {
			Logger.error(new MessageBuilder("Server {server} tried to update realm {owner_uuid} state but it is claimed by {claimed_server}")
					.parse("server", actionOriginatorServer)
					.parse("owner_uuid", ownerUUID)
					.parse("claimed_server", serverID)
			);
			return;
		}

		if (newState == null) {
			Logger.debug(new MessageBuilder("Realm {owner_uuid} changing state from {old_state} to null")
					.parse("owner_uuid", proxyRealm.getOwnerUUID())
					.parse("old_state", proxyRealm.getState())
			);
			realmMap.remove(ownerUUID);
			return;
		}

		proxyRealm.updateState(newState);
	}

	public @Nullable ProxyRealm getProxyRealm(UUID ownerUUID) {
		return realmMap.get(ownerUUID);
	}


	public String dump() {
		HashMap<String, List<ProxyRealm>> serverRealms = new HashMap<>();

		for (ProxyRealm proxyRealm : realmMap.values()) {
			serverRealms.putIfAbsent(proxyRealm.getServerID(), new ArrayList<>());
			serverRealms.get(proxyRealm.getServerID()).add(proxyRealm);
		}

		return serverRealms.entrySet().stream()
				.map((Map.Entry<String, List<ProxyRealm>> entry) -> dumpServer(entry.getKey(), entry.getValue()))
				.collect(Collectors.joining("\n\n"));
	}

	private String dumpServer(String server, List<ProxyRealm> realms) {
		// TODO Config
		MessageBuilder serverEntryTemplate = new MessageBuilder("""
				- {server_id} ({count})
				{realms}
				""");

		return serverEntryTemplate
				.parse("server_id", server)
				.parse("count", realms.size())
				.parse("realms",
						realms.stream()
								.map(ProxyRealm::dump)
								.collect(Collectors.joining("\n"))
				)
				.parse();
	}
}

