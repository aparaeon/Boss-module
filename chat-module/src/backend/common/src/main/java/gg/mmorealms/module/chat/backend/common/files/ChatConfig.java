package gg.mmorealms.module.chat.backend.common.files;

import java.util.List;

public class ChatConfig {

	public List<String> blockedGameMessages = List.of(
			"joined the game",
			"left the game",
			"multiplayer.player.joined",
			"multiplayer.player.left"
	);

}
