package gg.mmorealms.module.core.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.ExpirableCount;
import com.raduvoinea.utils.generic.dto.ExpirableList;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.event.impl.UserPreJoinRequest;
import gg.mmorealms.loader.common.dto.event.impl.UserPreLeaveRequest;
import gg.mmorealms.module.core.common.dto.event.UserFullyLoadedEvent;
import gg.mmorealms.module.core.common.dto.event.server.BackendRegistrationRequest;
import gg.mmorealms.module.core.common.dto.event.server.BackendUnregistrationEvent;
import gg.mmorealms.module.core.common.dto.event.server.Heartbeat;
import gg.mmorealms.module.core.common.dto.event.server.ServerPrettyNameRequest;
import gg.mmorealms.module.core.common.dto.event.user.UserTransferEvent;
import gg.mmorealms.module.core.velocity.CoreVelocityModule;
import gg.mmorealms.module.core.velocity.config.CoreConfig;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.core.velocity.dto.event.PlayerChoseInitialServerEventWrapper;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Listener {

	private final static Time FALLBACK_COOLDOWN = Time.seconds(5);
	private final static Time SERVER_KICK_COOLDOWN = Time.seconds(5);
	private final static int SERVER_KICK_THRESHOLD = 5;

	private final ExpirableCount connectionsThisSecond = new ExpirableCount(Time.seconds(1));
	private final ExpirableList<UUID> recentFallbacks = new ExpirableList<>(FALLBACK_COOLDOWN);
	private final HashMap<String, ExpirableCount> serverKicks = new HashMap<>();

	private @Inject CoreConfig config;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject ServerManager serverManager;

	@EventHandler
	private void onServerPrettyNameRequest(ServerPrettyNameRequest event) {
		EngineServer server = CoreVelocityModule.instance().getServerManager().getServer(event.getServerID());
		if (server == null) {
			event.setResult(null);
			return;
		}

		event.setResult(server.getPrettyName());
	}

	@EventHandler
	private void onAuth(BackendRegistrationRequest event) {
		InetSocketAddress socket = new InetSocketAddress(event.getHost(), event.getPort());

		ServerInfo serverInfo = new ServerInfo(
				event.getOriginator(),
				socket
		);

		boolean result = CoreVelocityModule.instance().getServerManager().register(serverInfo, event.getServerType());
		event.setResult(result);
	}

	@EventHandler
	private void onHeartbeat(Heartbeat event) {
		boolean result = CoreVelocityModule.instance().getServerManager().isRegistered(event.getOriginator());
		event.setResult(result);
	}

	@SneakyThrows
	@EventHandler
	private void onServerPreConnect(ServerPreConnectEvent event) {
		Optional<ServerConnection> optionalServerConnection = event.getPlayer().getCurrentServer();

		if (optionalServerConnection.isEmpty()) {
			return;
		}

		Optional<RegisteredServer> previousServer = optionalServerConnection.get().getPreviousServer();

		Logger.debug(new MessageBuilder("{uuid} pre-connected. Sending UserPreLeaveRequest to {server} (current: {curr_server}, prev: {prev_server})")
				.parse("uuid", event.getPlayer().getUniqueId())
				.parse("server", optionalServerConnection.get().getServerInfo().getName())
				.parse("curr_server", optionalServerConnection.get().getServer().getServerInfo().getName())
				.parse("prev_server", previousServer.isEmpty() ? "N/A" : previousServer.get().getServerInfo().getName())
		);

		DataProtectionListener.DISABLED_LIST.add(event.getPlayer().getUniqueId());
		boolean result = Boolean.TRUE.equals(new UserPreLeaveRequest(
				optionalServerConnection.get().getServerInfo().getName(),
				event.getPlayer().getUniqueId()
		).sendAndGet());

		if (!result) {
			event.setResult(ServerPreConnectEvent.ServerResult.denied());
			event.getPlayer().sendMessage(miniMessageManager.parse(config.lang.connectionFailure));
		}

	}

	@EventHandler
	private void onDisconnect(DisconnectEvent event) {
		Optional<ServerConnection> optionalServerConnection = event.getPlayer().getCurrentServer();

		if (optionalServerConnection.isEmpty()) {
			return;
		}

		Optional<RegisteredServer> previousServer = optionalServerConnection.get().getPreviousServer();

		Logger.debug(new MessageBuilder("{uuid} disconnected. Sending UserPreLeaveRequest to {server} (current: {curr_server}, prev: {prev_server})")
				.parse("uuid", event.getPlayer().getUniqueId())
				.parse("server", optionalServerConnection.get().getServerInfo().getName())
				.parse("curr_server", optionalServerConnection.get().getServer().getServerInfo().getName())
				.parse("prev_server", previousServer.isEmpty() ? "N/A" : previousServer.get().getServerInfo().getName())
		);

		new UserPreLeaveRequest(
				optionalServerConnection.get().getServerInfo().getName(),
				event.getPlayer().getUniqueId()
		).send();
	}

	@EventHandler
	public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
		Player player = event.getPlayer();

		if (!player.isActive()) {
			return;
		}

		PlayerChoseInitialServerEventWrapper playerChoseInitialServerEventWrapper = new PlayerChoseInitialServerEventWrapper(player);
		PlayerChoseInitialServerEventWrapper.Result result = playerChoseInitialServerEventWrapper.fireSync();

		if (result.isSuccess()) {
			event.setInitialServer(result.getInitialServer());
			return;
		}

		event.getPlayer().disconnect(miniMessageManager.parse(result.getMessage()));
	}

	@EventHandler(order = -100_000_000)
	public void onPlayerChoseInitialServerEventWrapper(PlayerChoseInitialServerEventWrapper event) {
		if (event.isFailure()) {
			return;
		}

		Player player = event.getPlayer();

		if (connectionsThisSecond.size() >= CoreVelocityModule.instance().getConfig().maxConnectionPerSecond) {
			event.fail(config.lang.rateLimit);
			return;
		}

		EngineServer engineServer = CoreVelocityModule.instance().getServerManager().getLowestUsageServer(ServerType.SPAWN);

		if (engineServer == null) {
			event.fail(config.lang.noSpawn);
			return;
		}

		boolean response = Boolean.TRUE.equals(new UserPreJoinRequest(engineServer.getRedisID(), player.getUniqueId()).sendAndGet());

		if (!response) {
			event.fail(config.lang.connectionFailure);
			return;
		}

		RegisteredServer server = engineServer.getProxyServer();

		if (server == null) {
			CoreVelocityModule.instance().getServerManager().unregister(engineServer.getRedisID());
			return;
		}

		CompletableFuture<ServerPing> pingCompletableFuture = server.ping();
		ServerPing ping = pingCompletableFuture.join();

		if (ping == null) {
			CoreVelocityModule.instance().getServerManager().unregister(engineServer.getRedisID());
			return;
		}

		event.setResult(PlayerChoseInitialServerEventWrapper.Result.success(server));
		connectionsThisSecond.add();
	}

	@EventHandler
	public void onKickedFromServer(KickedFromServerEvent event) {
		Player player = event.getPlayer();
		String serverID = event.getServer().getServerInfo().getName();

		serverKicks.computeIfAbsent(serverID, __ -> new ExpirableCount(SERVER_KICK_COOLDOWN));

		// This is here to prevent a player from being stuck in an infinite fallback loop
		if (recentFallbacks.contains(player.getUniqueId())) {
			event.setResult(KickedFromServerEvent.DisconnectPlayer.create(miniMessageManager.parse(config.lang.noSuitableServer)));
			return;
		}

		EngineServer engineServer = CoreVelocityModule.instance().getServerManager().getLowestUsageServer(ServerType.SPAWN);

		if (engineServer == null) {
			event.setResult(KickedFromServerEvent.DisconnectPlayer.create(miniMessageManager.parse(config.lang.noSpawn)));
			return;
		}

		RegisteredServer proxyServer = engineServer.getProxyServer();

		if (proxyServer == null) {
			event.setResult(KickedFromServerEvent.DisconnectPlayer.create(miniMessageManager.parse(config.lang.noSpawn)));
			return;
		}

		event.setResult(KickedFromServerEvent.RedirectPlayer.create(proxyServer));

		recentFallbacks.add(player.getUniqueId());
		serverKicks.get(serverID).add();

		if (serverKicks.get(serverID).size() == SERVER_KICK_THRESHOLD) {
			serverManager.unregister(serverID);
		}
	}

	@EventHandler
	public void onUserTransferEvent(UserTransferEvent event) {
		EngineServer server = CoreVelocityModule.instance().getServerManager().getServer(event.getServer().getServer());
		Player player = CoreVelocityModule.instance().getProxy().getPlayer(event.getUuid()).orElse(null);

		if (player == null) {
			return;
		}

		Optional<ServerConnection> optionalServerConnection = player.getCurrentServer();

		if (optionalServerConnection.isEmpty()) {
			return;
		}

		EngineServer currentServer = CoreVelocityModule.instance().getServerManager().getServer(optionalServerConnection.get().getServerInfo());

		if (server == null) {
			player.sendMessage(CoreVelocityModule.instance().getMiniMessageManager().parse(
					config.lang.serverNotFound
							.parse("server", event.getServer())
							.parse()
			));
			return;
		}

		if (currentServer != null && currentServer.getServerID().equals(server.getServerID()) && event.isSendSameServerMessage()) {
			player.sendMessage(
					CoreVelocityModule.instance().getMiniMessageManager().parse(config.lang.alreadyOnServer)
			);
		}

		server.join(player, event.getJoinEvents());
	}

	@EventHandler
	public void onBackendUnregistrationEvent(BackendUnregistrationEvent event) {
		CoreVelocityModule.instance().getServerManager().unregister(event.getOriginator());
	}

	@EventHandler
	public void onUserFullyLoadedEvent(UserFullyLoadedEvent event) {
		Logger.debug("Removing " + event.getUuid() + " from DISABLED_LIST");
		DataProtectionListener.DISABLED_LIST.remove(event.getUuid());
	}

	private static class ConnectionEntry {
		public UUID uuid;
		public long timestamp;

		public ConnectionEntry(UUID uuid) {
			this.uuid = uuid;
			this.timestamp = System.currentTimeMillis();
		}
	}

}