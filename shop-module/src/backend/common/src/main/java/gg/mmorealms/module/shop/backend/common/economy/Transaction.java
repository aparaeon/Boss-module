package gg.mmorealms.module.shop.backend.common.economy;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.config.ShopConfig;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.shop.ShopItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class Transaction {

	private static final ShopConfig CONFIG = ShopBackendModule.instance().config();

	private final User user;
	private final Shop shop;

	public Transaction(User user, Shop shop) {
		this.user = user;
		this.shop = shop;
	}

	public boolean buyItem(ShopItem item, boolean tradeMany) {
		if (item.buyItemPrice() < 0) {
			user.sendMessage(CONFIG.lang.errorInvalidBuyItem);
			return false;
		}

		IBalances balances = IBalances.getByUser(user);
		CurrencyType currency = shop.getDefaultCurrency();

		int amount = 1;
		ItemStack givenItems = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(item.itemId())), amount);
		if (tradeMany) {
			double balance = balances.get(currency);
			int canAfford = 99;
			if (item.buyItemPrice() != 0) {
				canAfford = (int) (balance / item.buyItemPrice());
			}
			givenItems.setCount(Math.min(canAfford, givenItems.getMaxStackSize()));
			amount = givenItems.getCount();
		}

		if (item.hasComponentChanges()) {
			givenItems.applyComponentsAndValidate(item.getComponentChanges());
		}

		if (amount == 0 || !balances.has(currency, item.buyItemPrice() * amount)) {
			user.sendMessage(CONFIG.lang.errorInsufficientFunds);
			return false;
		}

		balances.remove(currency, item.buyItemPrice() * amount, "SHOP");
		user.getPlayer().getInventory().placeItemBackInInventory(givenItems);
		tradeSuccessfulMessage(item, amount, false);
		return true;
	}

	public boolean sellItem(ShopItem item, boolean tradeMany) {
		if (item.sellItemPrice() < 0) {
			user.sendMessage(CONFIG.lang.errorItemNotSellable);
			return false;
		}

		IBalances balances = IBalances.getByUser(user);
		CurrencyType currency = shop.getDefaultCurrency();

		Item itemToSell = BuiltInRegistries.ITEM.get(ResourceLocation.parse(item.itemId()));
		int amount = 1;
		if (tradeMany) {
			amount = itemToSell.getDefaultMaxStackSize();
			ItemStack cursorStack = user.getPlayer().containerMenu.getCarried();
			if (cursorStack.getItem().equals(itemToSell)) {
				amount = Math.min(amount, Math.max(
						user.getPlayer().getInventory().countItem(itemToSell),
						cursorStack.getCount()
				));
			}
		}

		int amountRemovedFromInventory = removeItemsFromInventory(itemToSell, amount, item);
		if (amountRemovedFromInventory == 0) {
			user.sendMessage(CONFIG.lang.errorPlayerLacksItem);
			return false;
		}

		balances.add(currency, item.sellItemPrice() * amountRemovedFromInventory, "SHOP");
		tradeSuccessfulMessage(item, amountRemovedFromInventory, true);
		return true;
	}

	public boolean sellStack(ItemStack itemStack) {
		int amountInHand = itemStack.getCount();

		ShopItem sellItem = this.shop.findItem(itemStack);
		if (sellItem == null || sellItem.sellItemPrice() < 0) {
			user.sendMessage(CONFIG.lang.errorItemNotSellable);
			return false;
		}

		IBalances balances = IBalances.getByUser(user);
		CurrencyType currency = shop.getDefaultCurrency();

		int amountRemovedFromInventory = removeItemsFromInventory(itemStack);
		if (amountRemovedFromInventory == 0) {
			user.sendMessage(CONFIG.lang.errorPlayerLacksItem);
			return false;
		}

		balances.add(currency, sellItem.sellItemPrice() * amountInHand, "SHOP");
		tradeSuccessfulMessage(sellItem, amountInHand, true);
		return true;
	}

	private void tradeSuccessfulMessage(ShopItem item, int amount, boolean isSellTransaction) {
		String tradeType = isSellTransaction ? "sold" : "bought";
		double price = isSellTransaction ? item.sellItemPrice() : item.buyItemPrice();
		double totalPrice = price * amount;

		CurrencyType currency = shop.getDefaultCurrency();

		Logger.info(CONFIG.lang.logMessageTransactionSuccess
				.parse("user", user.getUsername())
				.parse("type", tradeType)
				.parse("amount", amount)
				.parse("item", item.getName())
				.parse("price", totalPrice)
				.parse("currency_color", currency.getColor())
				.parse("currency", currency)
		);

		user.sendMessage(CONFIG.lang.messageTransactionSuccess
				.parse("type", tradeType)
				.parse("amount", amount)
				.parse("item", item.getName())
				.parse("price", totalPrice)
				.parse("currency_color", currency.getColor())
				.parse("currency", currency)
		);
	}

	private int removeItemsFromInventory(Item itemToRemove, int amount, ShopItem shopItem) {
		return removeItemsFromInventory(itemToRemove, amount,
				stack -> stack.getItem().equals(itemToRemove) && shopItem.matches(stack));
	}

	private int removeItemsFromInventory(ItemStack itemToRemove) {
		return removeItemsFromInventory(itemToRemove.getItem(), itemToRemove.getCount(),
				stack -> stack.equals(itemToRemove));
	}

	private int removeItemsFromInventory(Item item, int amount, Predicate<ItemStack> matcher) {
		Inventory inventory = user.getPlayer().getInventory();
		int amountToSell = amount;

		ItemStack cursorStack = user.getPlayer().containerMenu.getCarried();
		if (!cursorStack.isEmpty() && matcher.test(cursorStack)) {
			final int stackCount = cursorStack.getCount();
			if (stackCount < amount) {
				amount -= stackCount;
				user.getPlayer().containerMenu.setCarried(ItemStack.EMPTY);
			} else if (stackCount > amount) {
				cursorStack.setCount(stackCount - amount);
				amount = 0;
			} else {
				user.getPlayer().containerMenu.setCarried(ItemStack.EMPTY);
				amount = 0;
			}
		}

		if (amount > 0) {
			for (int i = 0; i < inventory.getContainerSize() && amount > 0; i++) {
				ItemStack stack = inventory.getItem(i);

				if (!matcher.test(stack)) {
					continue;
				}

				final int stackCount = stack.getCount();

				if (stackCount < amount) {
					amount -= stackCount;
					inventory.removeItem(i, stackCount);
					continue;
				}

				if (stackCount > amount) {
					ItemStack newItem = new ItemStack(item, stackCount - amount);
					newItem.applyComponentsAndValidate(stack.getComponentsPatch());
					inventory.removeItemNoUpdate(i);
					inventory.setItem(i, newItem);
					amount = 0;
					continue;
				}

				inventory.removeItemNoUpdate(i);
				amount = 0;
			}
		}

		return amountToSell - amount;
	}
}
