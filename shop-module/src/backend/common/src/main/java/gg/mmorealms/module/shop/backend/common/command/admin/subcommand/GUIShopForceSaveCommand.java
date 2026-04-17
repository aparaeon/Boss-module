package gg.mmorealms.module.shop.backend.common.command.admin.subcommand;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.shop.backend.common.command.admin.GUIShopAdminBaseCommand;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"forcesave"}, parent = GUIShopAdminBaseCommand.class)
public class GUIShopForceSaveCommand extends BackendCommand {
	public GUIShopForceSaveCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		GUIShopAdminBaseCommand.saveToFile();
		sendMessage(sender, "<green>Shops successfully saved to config file!");
	}

}
