package gg.mmorealms.module.auction_house.backend.common.commands.sub.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.generic.dto.Range;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import gg.mmorealms.module.auction_house.backend.common.commands.sub.SellCommand;
import gg.mmorealms.module.auction_house.backend.common.gui.impl.ItemListingConfirmationGUI;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Command(aliases = {"item"}, arguments = {"price"}, onlyFor = Command.OnlyFor.PLAYERS, parent = SellCommand.class)
public class ItemCommand extends UserCommand {

	public ItemCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String priceString = arguments.getFirst();
		int price;

		try {
			price = Integer.parseInt(priceString);
		} catch (NumberFormatException e) {
			user.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.invalidPrice);
			return;
		}

		if (price <= 0) {
			user.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.invalidPrice);
			return;
		}

		ItemStack stack = user.getPlayer().getMainHandItem().copy();
		user.getPlayer().getMainHandItem().setCount(0);

		if (stack.isEmpty()) {
			user.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.mustBeHoldingItem);
			return;
		}

		AtomicLong count = new AtomicLong();
		DatabaseManager.instance().getSessionFactory().inSession((session ->
			count.set(session.createQuery("SELECT COUNT(*) FROM auction_house_entries WHERE ownerUUID = :ownerUUID", Long.class)
				.setParameter("ownerUUID", user.getUUID())
				.getSingleResult()))
		);

		if (count.get() >= user.getCountPermission(AuctionHouseBackendModule.AUCTION_HOUSE_MAX_ENTRIES_PERMISSION_BASE, Range.of(0, 100))) { // TODO Config
			user.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.maxEntries);
			user.getPlayer().getMainHandItem().setCount(stack.getCount());
			return;
		}

		new ItemListingConfirmationGUI(user, stack, price).open();
	}
}
