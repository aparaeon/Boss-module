package gg.mmorealms.loader.velocity.utils;

import com.velocitypowered.api.proxy.Player;

import java.net.InetSocketAddress;

public class NetworkUtils {

	public static String getPlayerIP(Player player) {
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
