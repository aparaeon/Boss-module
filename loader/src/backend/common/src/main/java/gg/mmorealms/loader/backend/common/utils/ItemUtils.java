package gg.mmorealms.loader.backend.common.utils;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

	public static void setItemStackList(List<ItemStack> to, List<ItemStack> from) {
		for (int i = 0; i < Math.min(from.size(), to.size()); i++) {
			ItemStack item = from.get(i);
			to.set(i, item);
		}
	}

	public static String getLoggingDescription(ItemStack itemstack) {
		if (itemstack == null) {
			return null;
		}

		MessageBuilder result = new MessageBuilder("{item}{enchantments}{contents}");
		List<String> enchants = getEnchantsAsStringList(itemstack);
		List<String> contents = getContainerContentsAsStringList(itemstack);

		return result
			.parse("item", itemstack.toString())
			.parse("enchantments", enchants == null ? "" : " with enchants: " + enchants)
			.parse("contents", contents == null ? "" : " with contents: " + contents)
			.parse();
	}

	private static List<String> getContainerContentsAsStringList(ItemStack itemstack) {
		ItemContainerContents contents = itemstack.get(DataComponents.CONTAINER);
		if (contents == null) {
			return null;
		}

		Iterable<ItemStack> itemStacks = contents.nonEmptyItems();
		List<String> contentList = new ArrayList<>();
		for (ItemStack nonEmptyItem : itemStacks) {
			contentList.add(getLoggingDescription(nonEmptyItem));
		}
		return contentList;
	}

	private static List<String> getEnchantsAsStringList(ItemStack itemstack) {
		ItemEnchantments itemEnchantments = itemstack.get(DataComponents.STORED_ENCHANTMENTS);
		if (itemEnchantments == null) {
			itemEnchantments = itemstack.get(DataComponents.ENCHANTMENTS);
			if (itemEnchantments == null || itemEnchantments.isEmpty()) {
				return null;
			}
		}

		List<String> enchantList = new ArrayList<>();
		for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : itemEnchantments.entrySet()) {
			enchantList.add(new MessageBuilder("{enchant}: {level}")
				.parse("enchant", holderEntry.getKey().getRegisteredName())
				.parse("level", holderEntry.getIntValue())
				.parse()
			);
		}

		return enchantList;
	}

}
