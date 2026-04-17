package gg.mmorealms.module.auction_house.backend.common.commands.sub;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.auction_house.backend.common.commands.AuctionHouseCommand;

@Command(aliases = {"sell"}, onlyFor = Command.OnlyFor.PLAYERS, parent = AuctionHouseCommand.class)
public class SellCommand extends BackendCommand {
	public SellCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}
}
