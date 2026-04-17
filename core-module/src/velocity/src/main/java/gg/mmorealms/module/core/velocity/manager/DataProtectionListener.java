package gg.mmorealms.module.core.velocity.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.SneakyThrows;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DataProtectionListener {

	private final static int PROTECTION_ORDER = 100000;
	public final static Set<UUID> DISABLED_LIST = new HashSet<>();

	@EventHandler(order = -PROTECTION_ORDER)
	private void onCommandExecuteEvent(CommandExecuteEvent event) {
		Logger.debug("CommandExecuteEvent: " + event.getCommand());
		CommandSource commandSource = event.getCommandSource();

		if (!(commandSource instanceof Player player)) {
			return;
		}

		if (!DISABLED_LIST.contains(player.getUniqueId())) {
			return;
		}

		Logger.warn(new MessageBuilder("Player {player} (UUID: {uuid}) attempted to execute a command ({command}) while data protection is enabled!")
				.parse("player", player.getUsername())
				.parse("uuid", player.getUniqueId().toString())
				.parse("command", event.getCommand())
		);
		event.setResult(CommandExecuteEvent.CommandResult.denied());
	}

	@SneakyThrows
	@EventHandler(order = -PROTECTION_ORDER)
	private void onServerPreConnect(ServerPreConnectEvent event) {
		Logger.debug(new MessageBuilder("ADDING to DISABLED_LIST {username}({uuid}) (1)")
				.parse("username", event.getPlayer().getUsername())
				.parse("uuid", event.getPlayer().getUniqueId()));
		DISABLED_LIST.add(event.getPlayer().getUniqueId());
	}

//	@SneakyThrows
//	@EventHandler
//	private void onServerPostConnectEvent(ServerPostConnectEvent event) {
//		Logger.debug(new MessageBuilder("REMOVING from DISABLED_LIST {username}({uuid}) (1)")
//				.parse("username", event.getPlayer().getUsername())
//				.parse("uuid", event.getPlayer().getUniqueId()));
//		DISABLED_LIST.remove(event.getPlayer().getUniqueId());
//	}

}