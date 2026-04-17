package gg.mmorealms.loader.common.dto;

import lombok.Getter;

@Getter
public enum ServerType {
	SPAWN("<green>spawn", "<newline><hover:show_text:'<light_purple><bold>Click to open the Realm/Wild selector!</bold></light_purple>'><click:run_command:'/select'><aqua><bold>You're at Spawn — the heart of MMO Realms!</bold></aqua><newline><newline><yellow><bold>Use /select to choose between your Realm or the Wild.</bold></yellow><newline><newline><light_purple><bold>──➤ Click here to open the selection menu!</bold></light_purple></click></hover><newline>"),
	WILD("<color:#786659>wilds", "<newline><hover:show_text:'<light_purple><bold>Click here to open your Realm Menu</bold></light_purple>'><click:run_command:'/realm'><red><bold>⚠ WARNING: Builds in the wild are NOT saved!</bold></red><newline><newline><yellow><bold>Use /realm to build in your own personal realm — always saved.</bold></yellow><newline><newline><light_purple><bold>──➤ Click here to open your Realm Menu!</bold></light_purple></click></hover><newline>"),
	WILD_GENERATOR("<color:#786659>wilds", "<newline><hover:show_text:'<light_purple><bold>Click here to open your Realm Menu</bold></light_purple>'><click:run_command:'/realm'><red><bold>⚠ WARNING: Builds in the wild are NOT saved!</bold></red><newline><newline><yellow><bold>Use /realm to build in your own personal realm — always saved.</bold></yellow><newline><newline><light_purple><bold>──➤ Click here to open your Realm Menu!</bold></light_purple></click></hover><newline>"),
	REALMS("<color:#4568d4>realms", ""),
	GYMS("<gold>gyms", ""),
	UNKNOWN("", "");

	private final String displayName;
	private final String joinMessage;

	ServerType(String displayName, String joinMessage) {
		this.displayName = displayName;
		this.joinMessage = joinMessage;
	}
}
