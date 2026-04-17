package gg.mmorealms.module.chat.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;

@Command(aliases = {"broadcast_no_permission"}, arguments = {"permission_node", "message..."})
public class BroadcastNoPermissionCommand extends VelocityCommand {

	private @Inject ProxyServer proxy;
	private @Inject VelocityMiniMessageManager miniMessageManager;

	public BroadcastNoPermissionCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String permission = arguments.getFirst();
		String message = String.join(" ", arguments.subList(1, arguments.size()));

		for (Player player : proxy.getAllPlayers()) {
			if (LuckPermsUtils.checkPermission(Player.class, player, permission)) {
				continue;
			}

			player.sendMessage(miniMessageManager.parse(message));
		}
	}
}
