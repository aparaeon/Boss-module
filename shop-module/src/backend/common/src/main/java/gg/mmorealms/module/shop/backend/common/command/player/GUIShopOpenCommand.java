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
import gg.mmorealms.module.shop.backend.common.gui.PagedShopGUI;
import gg.mmorealms.module.shop.backend.common.gui.ShopGUI;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.util.CommonMethods;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"open"}, arguments = {"name..."}, parent = GuiShopBaseCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class GUIShopOpenCommand extends UserCommand {
	private static @Inject ShopConfig config;

	public GUIShopOpenCommand(CommonCommandManager commandManager) {
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
		String shopName = arguments.getFirst();

		Shop selectedShop = CommonMethods.getShopByName(shopName);

		if (selectedShop == null) {
			user.sendMessage(config.lang.errorShopNotFound);
			return;
		}

		if (selectedShop.getItems().isEmpty()) {
			user.sendMessage(config.lang.errorShopEmpty);
			return;
		}

		if (selectedShop.getItems().size() > PagedShopGUI.MAX_PAGE_ITEMS) {
			new PagedShopGUI(user, selectedShop);
			return;
		}

		new ShopGUI(user, selectedShop);
	}
}
