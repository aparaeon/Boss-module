package gg.mmorealms.module.chat.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;
import gg.mmorealms.module.chat.velocity.manager.ChatManager;

import java.util.List;

@Command(aliases = {"mute_chat", "mutechat", "mmorealms_mute_chat"})
public class MuteChatCommand extends VelocityCommand {
	private @Inject ChatManager chatManager;

	public MuteChatCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		this.chatManager.setMuted(
				!this.chatManager.isMuted()
		);

		String action = this.chatManager.isMuted() ? "muted" : "unmuted";
		ChatVelocityModule.instance().sendMessage("Chat has been " + action);
	}
}
