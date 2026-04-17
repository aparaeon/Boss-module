package gg.mmorealms.module.shop.backend.common.command.admin.subcommand;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.command.admin.GUIShopAdminBaseCommand;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"create"}, arguments = {"name", "currency"}, parent = GUIShopAdminBaseCommand.class)
public class GUIShopCreateCommand extends BackendCommand {
	public GUIShopCreateCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String shopName = arguments.get(0);
		String currencyString = arguments.get(1);

		CurrencyType currency = CurrencyType.parse(currencyString);

		if (currency == null) {
			sendMessage(sender, "Invalid currency type"); // TODO Config
			return;
		}

		ShopBackendModule.instance().manager().addShop(shopName, currency);
		GUIShopAdminBaseCommand.saveToFile();
		sendMessage(sender, "<green>Shop successfully created!");
	}
}
