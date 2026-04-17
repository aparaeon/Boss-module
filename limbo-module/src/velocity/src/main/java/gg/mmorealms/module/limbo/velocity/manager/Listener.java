package gg.mmorealms.module.limbo.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.minecraft.MinecraftPing;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.velocity.dto.event.PlayerChoseInitialServerEventWrapper;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.limbo.velocity.LimboVelocityModule;
import gg.mmorealms.module.limbo.velocity.files.LimboConfig;

import java.io.IOException;

public class Listener {

	private @Inject LimboConfig config;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject ServerManager serverManager;

	@SuppressWarnings({"FieldCanBeLocal", "unused"}) // GC prevention
	private final CancelableTimeTask limboPlayerCountFetcher;
	private int limboPlayerCount = 0;

	public Listener() {
		this.limboPlayerCountFetcher = ScheduleUtils.runTaskTimer(() -> {
			MinecraftPing ping = new MinecraftPing(
					new MinecraftPing.Options()
							.hostname(LimboVelocityModule.instance().getConfig().limboServerHost)
							.port(LimboVelocityModule.instance().getConfig().limboServerPort)
			);
			MinecraftPing.Reply reply;

			try {
				reply = ping.send();
			} catch (IOException exception) {
				this.limboPlayerCount = 0;
				return;
			}

			this.limboPlayerCount = reply.getPlayers().getOnline();
		}, Time.seconds(1));

	}

	@EventHandler(order = 100_000)
	public void onPing(ProxyPingEvent event) {
		boolean allowConnectionsFromLimbo =
				!serverManager.getServers(ServerType.SPAWN).isEmpty() &&
						!EssentialsVelocityModule.instance().getConfig().whitelistEnabled;

		ServerPing.Builder pongBuilder = event.getPing().asBuilder();
		pongBuilder.onlinePlayers(pongBuilder.getOnlinePlayers() + this.limboPlayerCount);
		pongBuilder.version(new ServerPing.Version(
				event.getPing().getVersion().getProtocol(),
				event.getPing().getVersion().getName() + (allowConnectionsFromLimbo ? "" : "OFFLINE")
		));

		event.setPing(pongBuilder.build());
	}

	@EventHandler(order = 100_000)
	public void onKickedFromServer(KickedFromServerEvent event) {
		if (event.getResult() instanceof KickedFromServerEvent.RedirectPlayer redirectPlayer && !redirectPlayer.getServer().getServerInfo().getName().equals("Error")) {
			return; // The player has been redirected to another server, no need to transfer them to limbo
		}

		event.setResult(KickedFromServerEvent.Notify.create(miniMessageManager.parse("")));
		event.getPlayer().transferToHost(config.getLimboServerAddress());
	}

	@EventHandler(order = 100_000)
	public void onPlayerChoseInitialServerEventWrapper(PlayerChoseInitialServerEventWrapper event) {
		if (event.getResult().isSuccess()) {
			return; // The player has successfully connected to a server, no need to transfer them to limbo
		}

		event.setResult(PlayerChoseInitialServerEventWrapper.Result.deferred());
		event.getPlayer().transferToHost(config.getLimboServerAddress());
	}

}
