package gg.mmorealms.module.shop.backend.common.command.admin.subcommand;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.command.admin.GUIShopAdminBaseCommand;
import gg.mmorealms.module.shop.backend.common.config.ShopConfig;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"delete"}, arguments = {"name"}, parent = GUIShopAdminBaseCommand.class)
public class GUIShopDeleteCommand extends BackendCommand {
	public GUIShopDeleteCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("name")) {
			return ShopBackendModule.instance().manager().getAllShopsNames();
		}
		return new ArrayList<>();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		ShopConfig config = ShopBackendModule.instance().config();

		String shopName = arguments.getFirst();
		boolean found = ShopBackendModule.instance().manager().removeShop(shopName);

		if (found) {
			sendMessage(sender, config.lang.messageDeleteSuccess);
		} else {
			sendMessage(sender, config.lang.errorShopNotFound);
		}
	}
}
