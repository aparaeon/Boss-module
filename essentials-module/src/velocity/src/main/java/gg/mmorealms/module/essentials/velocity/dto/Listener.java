package gg.mmorealms.module.essentials.velocity.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import gg.mmorealms.module.core.common.dto.NetworkLocation;
import gg.mmorealms.module.core.common.dto.event.user.UserSetLastNetworkLocationEvent;
import gg.mmorealms.module.core.common.dto.event.user.UserTransferEvent;
import gg.mmorealms.module.essentials.common.dto.event.UserLastNetworkLocationRequest;
import gg.mmorealms.module.essentials.common.dto.event.UserNetworkLocationRequest;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;

import java.util.Optional;

public class Listener {

	private @Inject ProxyServer proxy;


	@EventHandler
	public void onUserSetLastNetworkLocationEvent(UserSetLastNetworkLocationEvent event) {
		EssentialsVelocityModule.getLastLocations().put(event.getUuid(), event.getNetworkLocation());
	}

	@EventHandler
	public void onUserLastNetworkLocationRequest(UserLastNetworkLocationRequest event) {
		event.setResult(EssentialsVelocityModule.getLastLocations().get(event.getUuid()));
	}

	@EventHandler(order = -10)
	public void onUserTransferEvent(UserTransferEvent event) {
		if (!event.isSaveLocation()) {
			return;
		}

		Optional<Player> playerOptional = proxy.getPlayer(event.getUuid());

		if (playerOptional.isEmpty()) {
			return;
		}

		Player player = playerOptional.get();
		Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();

		if (serverConnectionOptional.isEmpty()) {
			return;
		}

		ServerConnection serverConnection = serverConnectionOptional.get();
		NetworkLocation location = new UserNetworkLocationRequest(serverConnection.getServerInfo().getName(), event.getUuid()).sendAndGet();

		new UserSetLastNetworkLocationEvent(event.getUuid(), location).send();
	}
}