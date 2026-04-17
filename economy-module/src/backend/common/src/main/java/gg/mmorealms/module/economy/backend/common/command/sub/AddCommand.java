package gg.mmorealms.module.economy.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.economy.backend.common.command.BalanceCommand;
import lombok.Setter;

@Setter
@Command(aliases = {"add"}, parent = BalanceCommand.class)
public class AddCommand extends ModifyBalanceCommand {
	public AddCommand(CommonCommandManager commandManager) {
		super(commandManager, false);
	}
}
