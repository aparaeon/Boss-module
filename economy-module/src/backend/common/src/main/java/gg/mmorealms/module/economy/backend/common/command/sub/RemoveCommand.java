package gg.mmorealms.module.economy.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.economy.backend.common.command.BalanceCommand;

@Command(aliases = {"remove"}, parent = BalanceCommand.class)
public class RemoveCommand extends ModifyBalanceCommand {
	public RemoveCommand(CommonCommandManager commandManager) {
		super(commandManager, true);
	}
}
