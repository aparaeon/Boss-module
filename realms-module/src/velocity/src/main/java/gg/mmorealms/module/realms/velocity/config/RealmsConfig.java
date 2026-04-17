package gg.mmorealms.module.realms.velocity.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.realms.common.dto.RealmState;

import java.util.HashMap;

public class RealmsConfig {
	public Lang lang = new Lang();

	public static class Lang {
		public MessageBuilder invalidUUID = new MessageBuilder("Invalid UUID: {uuid}");
		public MessageBuilder forceUnlock = new MessageBuilder("Unlocked {user}'s realm");
		public MessageBuilder checkRealm = new MessageBuilder("{user}'s realm is on server {server} in state {state}");

		public HashMap<RealmState, MessageBuilder> realmStates = new HashMap<>() {{
			put(RealmState.UNLOADING, new MessageBuilder("<yellow>{pronoun} realm is getting saved. Please wait a moment..."));
			put(RealmState.CRASHED, new MessageBuilder("<yellow>{pronoun} realm was previously loaded on a server that crashed. Please wait a moment until we fully save it and rejoin the server again after."));
			put(RealmState.LOADING, new MessageBuilder("<yellow>{pronoun} realm is currently loading, please wait!"));
			put(RealmState.LOADED, new MessageBuilder("<green>{pronoun} realm is already loaded<green>. You can join it by using the command <white>/realm tp</white>"));
		}};

		public String noAvailableServer1 = "<red>We were unable to locate a proper server for your realm. Please stand by while we try again...";
		public String noAvailableServer2 = "<red>The dev was lazy so please relog to try again.";
	}

	public HashMap<RealmState, Time> realmStateTimeouts = new HashMap<>() {{
		put(RealmState.UNLOADING, Time.minutes(30));
		put(RealmState.CRASHED, Time.minutes(30));
		put(RealmState.LOADING, Time.minutes(10));
		put(RealmState.LOADED, Time.seconds(0));
	}};
}
