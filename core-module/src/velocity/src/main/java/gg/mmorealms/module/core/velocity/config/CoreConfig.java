package gg.mmorealms.module.core.velocity.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.common.files.CommonCoreConfig;

public class CoreConfig extends CommonCoreConfig {

	// Management
	public int maxConnectionPerSecond = 50;
	public Time fallbackCooldown = Time.seconds(5);

	public Lang lang = new Lang();

	public static class Lang extends CommonCoreConfig.Lang {
		public String commitingCache = "Commiting caches on all servers. Please wait a few minutes for all servers to finish commiting their caches. (No confirmation messages will be sent)";
		public String connectionFailure = "Connection failure. Listener#onServerPreConnect. Please contact an administrator.";
		public String rateLimit = "You are connecting too fast. Please wait a moment and try again.";
		public String noSpawn = "No spawn server found. Please try again later.";
		public String noSuitableServer = "We could not find a suitable server for you. Please try again later.";
		public String alreadyOnServer = "You are already on this server.";
		public MessageBuilder transferMessage = new MessageBuilder("<color:#c8caca>You are being sent to a {server_id}<color:#c8caca> server");
		public MessageBuilder serverNotFound = new MessageBuilder("No server found for {server}");
	}

}
