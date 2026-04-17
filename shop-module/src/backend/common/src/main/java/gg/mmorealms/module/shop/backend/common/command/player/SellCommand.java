package gg.mmorealms.module.shop.backend.common.command.player;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.shop.backend.common.command.GuiShopBaseCommand;
import gg.mmorealms.module.shop.backend.common.config.ShopConfig;
import gg.mmorealms.module.shop.backend.common.economy.Transaction;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.shop.ShopItem;
import gg.mmorealms.module.shop.backend.common.util.CommonMethods;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = "sell", parent = GuiShopBaseCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class SellCommand extends UserCommand {
	private static @Inject ShopConfig config;

	public SellCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		ServerPlayer player = user.getPlayer();
		ItemStack itemStack = player.getMainHandItem();

		if (itemStack.isEmpty()) {
			user.sendMessage(config.lang.errorNoItemInHand);
			return;
		}

		for (Shop shop : CommonMethods.getAllShops()) {
			for (ShopItem shopItem : shop.getItems()) {
				if (shopItem.matches(itemStack) && shopItem.sellItemPrice() > 0) {
					Transaction transaction = new Transaction(user, shop);
					transaction.sellStack(itemStack);
					return;
				}
			}
		}

		user.sendMessage(config.lang.errorInvalidSellItem);
	}
}
