package gg.mmorealms.module.shop.backend.common.command.player;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.command.GuiShopBaseCommand;
import gg.mmorealms.module.shop.backend.common.config.ShopConfig;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.shop.ShopItem;
import gg.mmorealms.module.shop.backend.common.util.CommonMethods;
import net.minecraft.commands.CommandSourceStack;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"list"}, arguments = {"name..."}, parent = GuiShopBaseCommand.class)
public class GUIShopListCommand extends UserCommand {
	private static @Inject ShopConfig config;

	public GUIShopListCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("name...")) {
			return ShopBackendModule.instance().manager().getAllShopsNames();
		}
		return new ArrayList<>();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String name = arguments.getFirst();

		runSpecificShop(name, user);
	}

	public void runSpecificShop(String name, User user) {
		Shop foundShop = CommonMethods.getShopByName(name);

		if (foundShop == null) {
			user.sendMessage(config.lang.errorShopNotFound);
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(config.lang.listTitle
				.parse("shop_name", foundShop.getName()));
		for (ShopItem item : foundShop.getItems()) {
			stringBuilder.append(config.lang.listEntry
					.parse("item_name", item.getName())
					.parse("buy_price", item.buyItemPrice())
					.parse("sell_price", item.sellItemPrice())
			);
		}

		String msg = StringUtils.chomp(stringBuilder.toString());
		user.sendMessage(msg);
	}
}
