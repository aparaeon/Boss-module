package gg.mmorealms.module.limbo.velocity.files;

import java.net.InetSocketAddress;

public class LimboConfig {

	public String limboServerHost = "limbo.cobblemon.mmorealms.gg";
	public int limboServerPort = 25565;

	public InetSocketAddress getLimboServerAddress() {
		return new InetSocketAddress(limboServerHost, limboServerPort);
	}

}
