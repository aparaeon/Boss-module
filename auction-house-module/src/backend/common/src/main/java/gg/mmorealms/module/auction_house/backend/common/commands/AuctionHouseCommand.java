package gg.mmorealms.module.auction_house.backend.common.commands;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import gg.mmorealms.module.auction_house.backend.common.gui.AuctionHouseGUI;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"auctionhouse", "ah", "gts"}, onlyFor = Command.OnlyFor.PLAYERS)
public class AuctionHouseCommand extends UserCommand {

	public AuctionHouseCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		if (AuctionHouseBackendModule.hasActiveCooldown(user)) {
			user.sendMessage("<red>You have purchased an item recently. Please wait a minute before trying again"); // TODO Config
			return;
		}

		new AuctionHouseGUI(user, AuctionHouseBackendModule.instance().getConfig().allCategory);
	}
}
