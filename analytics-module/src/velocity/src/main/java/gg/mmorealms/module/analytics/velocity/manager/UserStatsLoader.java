package gg.mmorealms.module.analytics.velocity.manager;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.velocity.manager.VelocityPlayerDependentDatabaseLoader;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public class UserStatsLoader extends VelocityPlayerDependentDatabaseLoader<UserStats> {

	public UserStatsLoader() {
		super(UserStats.class);
	}

	@Override
	public void onJoin(@NotNull Player player) {
		UserStats stats = UserStats.getByPlayer(player);

		stats.setLoginTimestamp(System.currentTimeMillis());
		stats.setIp(getPlayerIP(player));
		stats.setLastOnlineTimestamp(System.currentTimeMillis());
		Logger.log(new MessageBuilder("User {user} logged on from IP {ip}")
				.parse("user", player.getUsername())
				.parse("ip", stats.getIp())
		);

		try {
			stats.save();
		} catch (DatabaseSaveException e) {
			Logger.error(e);
		}
	}

	@Override
	public void onLeave(@NotNull Player player) {
		UserStats stats = UserStats.getByPlayer(player);
		stats.logout();
	}

	private String getPlayerIP(Player player) {
		String remoteAddress = player.getRemoteAddress().getAddress().getHostAddress();
		String virtualHostname = player.getVirtualHost()
				.map(InetSocketAddress::getHostName)
				.orElse("");

		// Support for k8s proxy load balancing
		if (virtualHostname.contains("///")) {
			return virtualHostname.split("///")[1];
		}

		return remoteAddress;
	}
}
