package gg.mmorealms.module.shop.backend.common.command.admin.subcommand;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.command.admin.GUIShopAdminBaseCommand;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.shop.ShopItem;
import gg.mmorealms.module.shop.backend.common.util.CommonMethods;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"addhelditem"}, arguments = {"shop", "buy", "sell"}, parent = GUIShopAdminBaseCommand.class)
public class GUIShopAddHeldItemCommand extends UserCommand {

	public GUIShopAddHeldItemCommand(CommonCommandManager commandManager) {
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
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String shopName = arguments.get(0);
		String buyItemPriceArg = arguments.get(1);
		String sellItemPriceArg = arguments.get(2);

		double buyItemPrice;
		double sellItemPrice;

		try {
			buyItemPrice = Double.parseDouble(buyItemPriceArg);
		} catch (NumberFormatException e) {
			user.sendMessage("<red>Invalid format for buy price");
			return;
		}

		try {
			sellItemPrice = Double.parseDouble(sellItemPriceArg);
		} catch (NumberFormatException e) {
			user.sendMessage("<red>Invalid format for sell price");
			return;
		}

		Shop foundShop = CommonMethods.getShopByName(shopName);
		if (foundShop == null) {
			user.sendMessage(new MessageBuilder("<red>Shop {shop} not found")
					.parse("shop", shopName)
			);
			return;
		}

		ItemStack heldItem = user.getPlayer().getMainHandItem();
		if (heldItem.isEmpty()) {
			user.sendMessage("<red>You must be holding an item to add it to the shop");
			return;
		}

		String itemId = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).toString();

		DataComponentPatch heldItemComponentChanges = heldItem.getComponentsPatch();

		foundShop.getItems().add(new ShopItem(
				itemId,
				buyItemPrice,
				sellItemPrice,
				new String[]{},
				heldItemComponentChanges
		));

		user.sendMessage("<green>Item successfully added");
	}
}