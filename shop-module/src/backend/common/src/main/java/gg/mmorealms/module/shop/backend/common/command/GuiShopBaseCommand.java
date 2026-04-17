package gg.mmorealms.module.shop.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.shop.backend.common.gui.MainMenuGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"guishop", "shop"})
public class GuiShopBaseCommand extends UserCommand {

	public GuiShopBaseCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new MainMenuGUI(user).open();
	}
}
