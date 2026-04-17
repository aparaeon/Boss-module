package gg.mmorealms.module.chat.velocity.command.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.velocity.manager.staff_chat.StaffChatManager;

import java.util.List;

@Command(aliases = {"staff_chat", "staffchat", "sc"}, onlyFor = Command.OnlyFor.PLAYERS)
public class StaffChatCommand extends VelocityCommand {

	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject StaffChatManager staffChatManager;

	public StaffChatCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		this.staffChatManager.toggleStaffChat(player);

		if (this.staffChatManager.isInStaffChat(player)) {
			player.sendMessage(this.miniMessageManager.parse("<green>Staff chat enabled.</green>")); // TODO Config
		} else {
			player.sendMessage(this.miniMessageManager.parse("<green>Staff chat disabled.</green>")); // TODO Config
		}

	}
}
