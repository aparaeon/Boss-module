package gg.mmorealms.module.essentials.velocity.command.whitelist.sub.advanced;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.command.whitelist.WhitelistCommand;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"advanced"}, parent = WhitelistCommand.class)
@Getter
@Setter
public class AdvancedCommand extends VelocityCommand {

	public AdvancedCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
	}


}
