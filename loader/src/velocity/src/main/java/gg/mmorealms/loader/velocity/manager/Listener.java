package gg.mmorealms.loader.velocity.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import gg.mmorealms.loader.common.dto.event.impl.UserServerRequest;
import gg.mmorealms.loader.velocity.VelocityLoader;

public class Listener {

	@EventHandler
	public void onUserServer(UserServerRequest event) {
		Player player;
		if (event.getUuid() != null) {
			player = VelocityLoader.instance().getProxy().getPlayer(event.getUuid()).orElse(null);
		} else {
			player = VelocityLoader.instance().getProxy().getPlayer(event.getUsername()).orElse(null);
		}

		if (player == null) {
			event.setResult(null);
			return;
		}

		ServerConnection connection = player.getCurrentServer().orElse(null);

		if (connection == null) {
			event.setResult(null);
			return;
		}

		event.setResult(connection.getServerInfo().getName());
	}

	@EventHandler
	public void onLoginEvent(LoginEvent event) {
		VelocityPlayerDependentDatabaseLoader.joined(event.getPlayer());
	}

	@EventHandler
	public void onDisconnectEvent(DisconnectEvent event) {
		VelocityPlayerDependentDatabaseLoader.left(event.getPlayer());
	}

}