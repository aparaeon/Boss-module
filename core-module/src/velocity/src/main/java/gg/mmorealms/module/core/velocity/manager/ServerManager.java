package gg.mmorealms.module.core.velocity.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.common.dto.PlayerList;
import gg.mmorealms.module.core.common.dto.ServerList;
import gg.mmorealms.module.core.common.dto.event.server.Heartbeat;
import gg.mmorealms.module.core.common.dto.event.server.PlayerListBroadcast;
import gg.mmorealms.module.core.common.dto.event.server.ServerListBroadcast;
import gg.mmorealms.module.core.velocity.CoreVelocityModule;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO Replace all streams
@Getter
public class ServerManager {

	private final List<EngineServer> servers = Collections.synchronizedList(new ArrayList<>());

	private final Thread heartBeatThread;
	private final CancelableTimeTask playerListBroadcastTask;
	private final CancelableTimeTask serverListBroadcastTask;

	private final Map<String, Integer> serverInfoNameToID = new HashMap<>();

	private final Set<String> disabledServers = new HashSet<>();
	private final Set<ServerType> disabledServerTypes = new HashSet<>();

	private int realmCount = 40;
	private int spawnCount = 40;
	private int wildCount = 40;
	private int gymsCount = 40;

	public ServerManager() {
		this.heartBeatThread = new Thread(this::executeHeartBeat);
		this.heartBeatThread.start();

		this.playerListBroadcastTask = ScheduleUtils.runTaskTimer(this::broadcastPlayerList, Time.seconds(5)); // TODO Config
		this.serverListBroadcastTask = ScheduleUtils.runTaskTimer(this::broadcastServerList, Time.seconds(5)); // TODO Config
	}

	private void broadcastPlayerList() {
		List<PlayerList.PlayerEntry> playerEntries = new ArrayList<>();

		for (Player player : CoreVelocityModule.instance().getProxy().getAllPlayers()) {
			playerEntries.add(new PlayerList.PlayerEntry(
					player.getUniqueId(),
					player.getUsername()
			));
		}

		new PlayerListBroadcast(playerEntries).send();
	}

	private void broadcastServerList() {
		List<ServerList.ServerEntry> serverEntries = new ArrayList<>();

		for (EngineServer server : this.servers) {
			if (server.getProxyServer() != null) {
				serverEntries.add(new ServerList.ServerEntry(server.getType(), server.getServerID(), server.getProxyServer().getPlayersConnected().size()));
			}
		}

		new ServerListBroadcast(serverEntries).send();
	}

	private void executeHeartBeat() {
		while (true) {
			try {
				HashMap<String, CompletableFuture<Boolean>> activeRequests = new HashMap<>();

				for (EngineServer server : servers) {
					CompletableFuture<Boolean> request = sendHeartBeat(server);

					if (request == null) {
						continue;
					}

					activeRequests.put(server.getServerID(), request);
				}

				//noinspection BusyWait
				Thread.sleep(CommonLoader.instance().getRedisConfig().getTimeout());

				activeRequests.forEach((server, request) -> {
					boolean result;

					try {
						result = request.get(5, TimeUnit.SECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException exception) {
						Logger.error(exception);
						Logger.warn("[Timeout] Server " + server + " timed out!");
						unregister(server);
						return;
					}

					if (!result) {
						Logger.warn("[Bad-Response] Server " + server + " timed out!");
						unregister(server);
					}
				});
			} catch (Exception e) {
				Logger.error("Error in heartbeat: " + e.getMessage());
			}
		}
	}

	private CompletableFuture<Boolean> sendHeartBeat(EngineServer server) {
		RegisteredServer registeredServer = server.getProxyServer();

		if (registeredServer == null) {
			return null;
		}

		ServerInfo serverInfo = registeredServer.getServerInfo();
		return new Heartbeat(serverInfo.getName()).send();
	}

	private void cleanUp() {
		servers.removeIf(Objects::isNull);
	}

	public @NotNull Set<EngineServer> getServers(ServerType serverType) {
		return servers
				.stream()
				.filter(Objects::nonNull)
				.filter(server -> server.getProxyServer() != null)
				.filter(server -> server.getType().equals(serverType))
				.collect(Collectors.toSet());
	}

	public @Nullable EngineServer getServer(ServerInfo serverInfo) {
		return servers
				.stream()
				.filter(Objects::nonNull)
				.filter(server -> server.getProxyServer() != null)
				.filter(server -> server.getServerID().equals(serverInfo.getName()))
				.findFirst()
				.orElse(null);
	}

	public @Nullable EngineServer getServer(String serverID) {
		return servers
				.stream()
				.filter(Objects::nonNull)
				.filter(server -> server.getProxyServer() != null)
				.filter(server -> server.getServerID().equals(serverID))
				.findFirst()
				.orElse(null);
	}

	public boolean isRegistered(ServerInfo serverInfo) {
		return getServer(serverInfo) != null;
	}

	public boolean isRegistered(String serverID) {
		return getServer(serverID) != null;
	}

	public boolean register(ServerInfo serverInfo, ServerType serverType) {
		cleanUp();

		if (isRegistered(serverInfo)) {
			EngineServer server = getServer(serverInfo);

			if (server == null) {
				Logger.error("How did we end up here?");
				unregister(serverInfo);
				return false;
			}

			if (server.getProxyServer() == null) {
				unregister(serverInfo);
				return register(serverInfo, serverType);
			}

			Logger.warn("Server " + serverInfo.getName() + " is already registered.");
			return false;
		}

		String serverInfoName = serverInfo.getName();
		// ID used by this server in the past
		Integer foundID = serverInfoNameToID.get(serverInfoName);

		int currentCount = -1;
		if (foundID != null) {
			currentCount = foundID;
		} else {
			// Try to get the ID from the name e.g. "realms-7"
			String[] parts = serverInfoName.split("-", 2);
			if (parts.length == 2) {
				try {
					currentCount = Integer.parseInt(parts[1].split("\\.")[0]);
				} catch (NumberFormatException ignored) {
				}
			}

			if (currentCount == -1) {
				currentCount = switch (serverType) {
					case SPAWN -> spawnCount++;
					case WILD -> wildCount++;
					case REALMS -> realmCount++;
					case GYMS -> gymsCount++;
					case UNKNOWN, WILD_GENERATOR -> -1;
				};
			}
		}

		serverInfoNameToID.put(serverInfoName, currentCount);
		servers.add(new EngineServer(serverInfo, serverType, currentCount));

		CoreVelocityModule.instance().getProxy().registerServer(serverInfo);
		Logger.good(
				new MessageBuilder("Registered server {server_id} ({server_type})")
						.parse("server_id", serverInfo.getName())
						.parse("server_type", serverType.name())
						.parse()
		);
		return true;
	}

	public void unregister(ServerInfo serverInfo) {
		cleanUp();

		if (!isRegistered(serverInfo)) {
			return;
		}
		servers.remove(getServer(serverInfo));
		CoreVelocityModule.instance().getProxy().unregisterServer(serverInfo);
	}

	public void unregister(String serverName) {
		cleanUp();

		if (!isRegistered(serverName)) {
			return;
		}

		EngineServer server = getServer(serverName);

		if (server == null) {
			return;
		}

		servers.remove(server);

		if (server.getProxyServer() == null) {
			return;
		}

		CoreVelocityModule.instance().getProxy().unregisterServer(server.getProxyServer().getServerInfo());
	}

	public @Nullable EngineServer getLowestUsageServer(ServerType serverType) {
		Stream<EngineServer> serverStream = getServers(serverType).stream();

		if (!this.disabledServers.isEmpty()) {
			serverStream = serverStream
					.filter(server -> !disabledServers.contains(server.getServerID()));
		}

		if (!this.disabledServerTypes.isEmpty()) {
			serverStream = serverStream
					.filter(server -> !disabledServerTypes.contains(server.getType()));
		}

		return serverStream
				.min(EngineServer::compareTo)
				.orElse(null);
	}

	public synchronized void disableServer(String serverID) {
		this.disabledServers.add(serverID);
	}

	public void enableServer(String serverID) {
		this.disabledServers.remove(serverID);
	}

	public void disableServerType(ServerType serverType) {
		this.disabledServerTypes.add(serverType);
	}

	public void enableServerType(ServerType serverType) {
		this.disabledServerTypes.remove(serverType);
	}

}