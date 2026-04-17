package gg.mmorealms.module.chat.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import gg.mmorealms.module.core.common.dto.chat.ChatCaptureResultEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VelocityChatInputManager {

	private final List<UUID> awaitingInput = new ArrayList<>();

	public VelocityChatInputManager() {

	}

	public void registerInputCallback(UUID uuid) {
		if (awaitingInput.contains(uuid)) {
			return;
		}

		awaitingInput.add(uuid);
	}

	public void unregisterInputCallback(UUID uuid) {
		awaitingInput.remove(uuid);
	}

	public boolean provideInput(Player player, String input) {
		UUID uuid = player.getUniqueId();

		if (!awaitingInput.contains(uuid)) {
			return false;
		}

		ServerConnection serverConnection = player.getCurrentServer().orElse(null);

		if (serverConnection == null) {
			return false;
		}

		String target = serverConnection.getServerInfo().getName();

		new ChatCaptureResultEvent(target, uuid, input).send();
		unregisterInputCallback(uuid);
		return true;
	}

}
