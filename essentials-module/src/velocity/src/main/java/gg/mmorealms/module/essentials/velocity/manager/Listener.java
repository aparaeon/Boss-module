package gg.mmorealms.module.essentials.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import gg.mmorealms.module.core.velocity.dto.event.PlayerChoseInitialServerEventWrapper;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.config.EssentialsConfig;
import net.kyori.adventure.text.Component;

import java.util.Arrays;

public class Listener {

	private final static int WHITELIST_ORDER = -1000;

	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject EssentialsConfig config;
	private @Inject ProxyServer proxy;

	@EventHandler(order = WHITELIST_ORDER)
	public void onPlayerChooseInitialServer(PlayerChoseInitialServerEventWrapper event) {
		if (event.isFailure()) {
			return;
		}

		Player player = event.getPlayer();

		if (EssentialsVelocityModule.instance().getConfig().whitelistEnabled) {
			if (!EssentialsVelocityModule.instance().getConfig().whitelist.contains(player.getUsername())) {
				event.fail("You are not whitelisted to join this server.");
			}
		}
	}

	@EventHandler
	public void onProxyPingEvent(ProxyPingEvent event) {
		ServerPing pong = event.getPing().asBuilder()
				.description(miniMessageManager.parse(config.motd.toString()))
				.build();

		event.setPing(pong);
	}

	@EventHandler
	public void onCommandExecuteEvent(CommandExecuteEvent event) {
		CommandSource commandStack = proxy.getConsoleCommandSource();

		if (event.getExecuteAs() != null) {
			Player player = proxy.getPlayer(event.getExecuteAs()).orElse(null);

			if (player == null) {
				Logger.warn("Tried to execute command as a player that is not online: " + event.getExecuteAs());
				return;
			}

			commandStack = player;
		}

		Logger.debug(new MessageBuilder("Executing command {command} as {execute_as}")
				.parse("command", event.getCommand())
				.parse("execute_as", commandStack)
				.parse()
		);
		proxy.getCommandManager().executeAsync(
				commandStack,
				event.getCommand()
		);
	}
}