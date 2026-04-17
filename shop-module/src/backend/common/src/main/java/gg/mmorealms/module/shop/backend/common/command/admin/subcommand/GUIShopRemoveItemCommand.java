package gg.mmorealms.module.shop.backend.common.command.admin.subcommand;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.command.admin.GUIShopAdminBaseCommand;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.shop.ShopItem;
import gg.mmorealms.module.shop.backend.common.util.CommonMethods;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"removeitem"}, arguments = {"shop", "item"}, parent = GUIShopAdminBaseCommand.class)
public class GUIShopRemoveItemCommand extends BackendCommand {

	public GUIShopRemoveItemCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("shop")) {
			return ShopBackendModule.instance().manager().getAllShopsNames();
		}
		return new ArrayList<>();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String shopArg = arguments.get(0);
		String itemArg = arguments.get(1);
		ShopItem foundItem = null;

		Shop foundShop = CommonMethods.getShopByName(shopArg);

		if (foundShop == null) {
			sendMessage(sender, "<red>Shop not found");
			return;
		}

		for (ShopItem item : foundShop.getItems()) {
			if (item.getName().equals(itemArg)) {
				foundItem = item;
				break;
			}
		}

		if (foundItem == null) {
			sendMessage(sender, "<red>Item not found");
			return;
		}

		foundShop.getItems().remove(foundItem);
		sendMessage(sender, "<green>Item successfully removed");
	}
}
