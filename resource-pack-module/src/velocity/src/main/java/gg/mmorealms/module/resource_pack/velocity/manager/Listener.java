package gg.mmorealms.module.resource_pack.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;

public class Listener {

	private @Inject ResourcePackManager resourcePackManager;

	@EventHandler
	public void onServerPostConnectEvent(ServerPostConnectEvent event) {
		resourcePackManager.send(event.getPlayer());
	}

}
