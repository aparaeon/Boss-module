package gg.mmorealms.module.realms.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.common.dto.event.server.BackendRegistrationRequest;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.realms.common.dto.RealmState;
import gg.mmorealms.module.realms.common.dto.event.*;
import gg.mmorealms.module.realms.velocity.RealmsVelocityModule;
import gg.mmorealms.module.realms.velocity.config.RealmsConfig;
import gg.mmorealms.module.realms.velocity.dto.ProxyRealm;
import lombok.SneakyThrows;

import java.util.Optional;

public class Listener {
	private @Inject RealmsConfig config;

	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject RealmsManager realmsManager;

	@EventHandler(order = 100_000_000)
	private void onBackendRegistrationRequest(BackendRegistrationRequest event) {
		if (!event.isFirstRegistration() || event.getServerType() != ServerType.REALMS) {
			return;
		}

		realmsManager.unregisterAll(event.getOriginator());
	}

	@EventHandler
	private void onRealmStateChangeEvent(RealmStateChangeEvent event) {
		realmsManager.setRealmState(event.getOriginator(), event.getUuid(), event.getState());
	}

	@EventHandler
	private void onPostLoginEvent(PostLoginEvent event) {
		ScheduleUtils.runTaskAsync(() -> asyncPostLoginEventHandler(event));
	}

	@SneakyThrows(InterruptedException.class)
	private void asyncPostLoginEventHandler(PostLoginEvent event) {
		Player player = event.getPlayer();

		while (player.isActive() && player.getCurrentServer().isEmpty()) {
			//noinspection BusyWait
			Thread.sleep(1000);
		}

		if (!player.isActive()) {
			return;
		}

		new LoadRealmEvent(player.getUniqueId()).fireSync();
	}

	@EventHandler
	private void onLoadRealmEvent(LoadRealmEvent event) {
//		Optional<Player> ownerOptional = RealmsVelocityModule.instance().getProxyServer().getPlayer(event.getOwnerUUID());
		Optional<Player> requesterOptional = RealmsVelocityModule.instance().getProxyServer().getPlayer(event.getRequesterUUID());

		if (requesterOptional.isEmpty()) {
			return;
		}

		Player requester = requesterOptional.get();

		// Modify the message shown if the requester is the owner
		ProxyRealm proxyRealm = RealmsVelocityModule.instance().getRealmsManager().getProxyRealm(event.getOwnerUUID());

		if (proxyRealm != null) {
			String pronoun = event.getRequesterUUID().equals(event.getOwnerUUID()) ? "Your" : "The";

			requester.sendMessage(miniMessageManager.parse(config.lang.realmStates.get(proxyRealm.getState())
					.parse("pronoun", pronoun)
					.parse()
			));

			return;
		}

		EngineServer engineServer = RealmsVelocityModule.instance().getServerManager().getLowestUsageServer(ServerType.REALMS);

		if (engineServer == null) {
			requester.sendMessage(miniMessageManager.parse(config.lang.noAvailableServer1));
			requester.sendMessage(miniMessageManager.parse(config.lang.noAvailableServer2)); // TODO Remove
			// TODO Add to a retry queue
			return;
		}

		RealmsVelocityModule.instance().getRealmsManager().setRealmState(engineServer.getServerID(), event.getOwnerUUID(), RealmState.LOADING);

		LoadRealmEvent newEvent = event.copy();
		newEvent.setTarget(engineServer.getServerID());
		newEvent.send();
	}

	@EventHandler
	private void onGetRealmServerRequest(GetRealmServerRequest event) {
		ProxyRealm proxyRealm = realmsManager.getProxyRealm(event.getOwnerUUID());

		if (proxyRealm == null) {
			event.setResult(null);
			return;
		}

		event.setResult(proxyRealm.getServerID());
	}


	@EventHandler
	private void onGetRealmServerRequest(GetRealmStateRequest event) {
		ProxyRealm proxyRealm = realmsManager.getProxyRealm(event.getOwnerUUID());

		if (proxyRealm == null) {
			event.setResult(null);
			return;
		}

		event.setResult(proxyRealm.getState());
	}

	@EventHandler
	private void onMultipleRealmStateChangeEvent(MultipleRealmStateChangeEvent event) {
		realmsManager.getRealmMap().forEach((ownerUUID, proxyRealm) -> {
			if (proxyRealm.getServerID().equals(event.getOriginator())) {
				realmsManager.setRealmState(event.getOriginator(), ownerUUID, event.getState());
			}
		});
	}
}