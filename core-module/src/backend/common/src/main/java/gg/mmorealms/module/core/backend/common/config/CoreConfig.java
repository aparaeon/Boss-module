package gg.mmorealms.module.core.backend.common.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.common.files.CommonCoreConfig;

public class CoreConfig extends CommonCoreConfig {
	public Lang lang = new Lang();

	public int maxMinecartsPerBlock = 5;

	public static class Lang extends CommonCoreConfig.Lang {
		public String commitingCache = "Commiting cache...";
		public String cacheCommited = "Cache committed!";
		public String commandCanOnlyBeExecuteByPlayer = "This command can only be execute by a player";
		public String underDevelopment = "This feature is under development.";
		public String guiError = "There was an error processing your request. Please contact an administrator";

		public MessageBuilder cooldown = new MessageBuilder("You have a cooldown of <aqua>{cooldown} <white>until you can do this again.");
		public String minecartStackingPlayerMessage = "<red>The action was cancelled because the minecart limit was reached.";
	}

}
