package gg.mmorealms.module.legendaries.velocity.utils;

import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.legendaries.velocity.LegendariesVelocityModule;

import java.util.Collection;
import java.util.List;

public class NetworkUtils {
	private static final ServerManager serverManager = LegendariesVelocityModule.instance().getServerManager();

	public static EngineServer getRandomServer(ServerType type, boolean canSendToEmpty) {
		List<EngineServer> servers = getServers(type);

		if (servers.isEmpty()) {
			Logger.warn("No servers of type " + type.name() + " are available.");
			return null;
		}

		if (!canSendToEmpty) {
			servers = servers.stream()
					.filter(s -> s.getPlayerCount() > 0)
					.toList();

			if (servers.isEmpty()) {
				Logger.warn("No non-empty servers of type " + type.name() + " are available.");
				return null;
			}
		}

		return RandomUtils.getRandom(servers);
	}

	public static List<EngineServer> getServers(ServerType type) {
		return serverManager.getServers(type).stream().toList();
	}

	public static Collection<Player> getAllPlayers() {
		return LegendariesVelocityModule.instance().getProxy().getAllPlayers();
	}

	public static int getPlayerCount() {
		return LegendariesVelocityModule.instance().getProxy().getPlayerCount();
	}
}
