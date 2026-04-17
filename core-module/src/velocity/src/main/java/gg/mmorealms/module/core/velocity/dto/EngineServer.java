package gg.mmorealms.module.core.velocity.dto;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.event.impl.RemoteExecuteEvent;
import gg.mmorealms.loader.common.dto.event.impl.UserPreJoinRequest;
import gg.mmorealms.module.core.velocity.CoreVelocityModule;
import gg.mmorealms.module.core.velocity.config.CoreConfig;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class EngineServer implements Comparable<EngineServer> {

	private final String prettyName;
	private final ServerInfo info;
	private final ServerType type;
	private final CoreConfig config = CoreVelocityModule.instance().getConfig();
	private final VelocityMiniMessageManager miniMessageManager = CoreVelocityModule.instance().getMiniMessageManager();

	public EngineServer(ServerInfo info, ServerType type, int index) {
		this.info = info;
		this.type = type;
		this.prettyName = type.getDisplayName() + "-" + index;
	}

	public @Nullable RegisteredServer getProxyServer() {
		return CoreVelocityModule.instance().getProxy()
				.getServer(info.getName())
				.orElse(null);
	}

	public String getRedisID() {
		return getServerID();
	}

	public String getServerID() {
		return info.getName();
	}

	public String getFriendlyName() {
		return this.getServerID().split("\\.")[0];
	}

	@Override
	public String toString() {
		return getFriendlyName();
	}

	@Override
	public int compareTo(@NotNull EngineServer other) {
		return Integer.compare(getPlayerCount(), other.getPlayerCount());
	}

	public int getPlayerCount() {
		Optional<RegisteredServer> serverOptional = CoreVelocityModule.instance().getProxy().getServer(getServerID());

		if (serverOptional.isEmpty()) {
			return 0;
		}

		RegisteredServer server = serverOptional.get();
		return server.getPlayersConnected().size();
	}

	@SuppressWarnings("unused")
	public void join(@NotNull Player player) {
		join(player, new ArrayList<>());
	}

	public void join(@NotNull Player player, List<String> serializedJoinEvents) {
		ServerConnection currentServerConnection = player.getCurrentServer().orElse(null);

		if (currentServerConnection != null) {
			EngineServer currentServer = CoreVelocityModule.instance().getServerManager().getServer(currentServerConnection.getServerInfo().getName());

			if (currentServer != null && currentServer.getServerID().equals(getRedisID())) {
				// Player is on the same server, just execute the serialized events without attempting to transfer the player
				new RemoteExecuteEvent(getRedisID(), serializedJoinEvents).send();
				return;
			}
		}

		player.sendMessage(miniMessageManager.parse(
				config.lang.transferMessage.parse("server_id", getPrettyName()
				))
		);

		UUID uuid = player.getUniqueId();
		UserPreJoinRequest event = new UserPreJoinRequest(getRedisID(), uuid, serializedJoinEvents);
		boolean response = Boolean.TRUE.equals(event.sendAndGet());

		if (!response) {
			player.sendMessage(miniMessageManager.parse(
					config.lang.serverNotFound
							.parse("server", getRedisID())
							.parse()
			));
			return;
		}

		RegisteredServer server = getProxyServer();

		if (server == null) {
			Logger.error("Failed to get server for " + getRedisID());
			return;
		}

		player.createConnectionRequest(server).fireAndForget();
	}

}
