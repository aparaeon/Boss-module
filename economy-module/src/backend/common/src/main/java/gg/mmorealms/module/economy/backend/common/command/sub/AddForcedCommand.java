package gg.mmorealms.module.economy.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.economy.backend.common.command.BalanceCommand;

@Command(aliases = {"add_forced"}, parent = BalanceCommand.class)
public class AddForcedCommand extends ModifyBalanceCommand {
	public AddForcedCommand(CommonCommandManager commandManager) {
		super(commandManager, false);
	}
}
