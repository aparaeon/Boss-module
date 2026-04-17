package gg.mmorealms.module.shop.backend.common.shop;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.config.ShopConfig;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * An item that can be bought or sold in a shop
 */
public record ShopItem(
		String itemId,
		double buyItemPrice,
		double sellItemPrice,
		String[] description,
		String componentChangesSerialized
) {

	public ShopItem(String itemId, double buyItemPrice, double sellItemPrice) {
		this(itemId, buyItemPrice, sellItemPrice, new String[0], "");
	}

	public ShopItem(String itemId, double buyItemPrice, double sellItemPrice, String[] description, DataComponentPatch componentChanges) {
		this(itemId, buyItemPrice, sellItemPrice, description, serializeComponentChanges(componentChanges));
	}

	private static String serializeComponentChanges(DataComponentPatch patch) {
		JsonElement json = DataComponentPatch.CODEC.encodeStart(
				ShopBackendModule.instance().getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE),
				patch
		).resultOrPartial(msg -> {
			throw new RuntimeException("Failed to serialize componentChanges: " + msg);
		}).orElseThrow();

		return json.toString();
	}

	public boolean hasComponentChanges() {
		return !(Objects.isNull(componentChangesSerialized) || componentChangesSerialized.isEmpty());
	}

	public DataComponentPatch getComponentChanges() {
		if (componentChangesSerialized == null || componentChangesSerialized.isEmpty()) {
			return DataComponentPatch.builder().build();
		}
		JsonElement json = JsonParser.parseString(componentChangesSerialized);
		return DataComponentPatch.CODEC.parse(
				ShopBackendModule.instance().getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE),
				json
		).resultOrPartial(msg -> {
			throw new RuntimeException("Failed to deserialize componentChanges: " + msg);
		}).orElse(DataComponentPatch.builder().build());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ShopItem shopItem = (ShopItem) o;

		if (shopItem.buyItemPrice != buyItemPrice) return false;
		if (shopItem.sellItemPrice != sellItemPrice) return false;
		if (!itemId.equals(shopItem.itemId)) return false;

		// Compare descriptions
		if (description.length != shopItem.description.length) return false;
		for (int i = 0; i < description.length; i++) {
			if (!description[i].equals(shopItem.description[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean matches(ItemStack other) {
		if (!matches(other.getItem())) return false;
		DataComponentPatch componentChanges = this.getComponentChanges();

		DataComponentPatch otherComponentChanges = other.getComponentsPatch();
		if (componentChanges == null && otherComponentChanges == null) {
			return true;
		}
		if (componentChanges == null || otherComponentChanges == null) {
			return false;
		}

		Optional<? extends Integer> damage = componentChanges.get(DataComponents.DAMAGE);
		if (damage != null && damage.isPresent()) {
			if (other.getDamageValue() < damage.get()) return false;
		}
		Optional<? extends Integer> maxDamage = componentChanges.get(DataComponents.MAX_DAMAGE);
		if (maxDamage != null && maxDamage.isPresent()) {
			if (other.getMaxDamage() < maxDamage.get()) return false;
		}

		// Only compare the relevant components
		DataComponentType<?>[] relevantComponentTypes = new DataComponentType[]{
				DataComponents.CUSTOM_MODEL_DATA,
				DataComponents.ENCHANTMENT_GLINT_OVERRIDE,
				DataComponents.ENCHANTMENTS,
				DataComponents.STORED_ENCHANTMENTS,
				DataComponents.ATTRIBUTE_MODIFIERS,
				DataComponents.UNBREAKABLE,
				DataComponents.RARITY,
				DataComponents.FOOD,
				DataComponents.FIRE_RESISTANT,
				DataComponents.TOOL,
				DataComponents.DYED_COLOR,
				DataComponents.TRIM,
		};
		for (DataComponentType<?> type : relevantComponentTypes) {
			if (!Objects.equals(componentChanges.get(type), otherComponentChanges.get(type))) {
				return false;
			}
		}

		return true;
	}

	public boolean matches(Item other) {
		return BuiltInRegistries.ITEM.getKey(other).toString().equals(itemId);
	}

	@Override
	public int hashCode() {
		DataComponentPatch componentChanges = this.getComponentChanges();

		int result = (itemId != null ? itemId.hashCode() : 0);
		result = 31 * result + (buyItemPrice != 0 ? Double.hashCode(buyItemPrice) : 0);
		result = 31 * result + (sellItemPrice != 0 ? Double.hashCode(sellItemPrice) : 0);
		result = 31 * result + Arrays.hashCode(description);
		result = 31 * result + (componentChanges != null ? componentChanges.hashCode() : 0);
		return result;
	}

	public List<String> getLore() {
		ShopConfig config = ShopBackendModule.instance().config();
		List<Pair2<MessageBuilder, String>> shopItemLore = config.lang.shopItemLore;

		List<String> result = new ArrayList<>(shopItemLore.size());

		List<String> description = getDescriptionAsText();

		if (description == null) {
			result.add("");
		} else {
			result.add(shopItemLore.get(0).first()
					.parse("description", description)
					.parse());
		}
		boolean anyOptionAvailable = false;

		// Show nothing if price = 0
		// Show alternative message if = -1
		if (buyItemPrice != 0) {
			if (buyItemPrice != -1) {
				result.add(shopItemLore.get(1).first()
						.parse("buyItemPrice", NumberUtils.formatNumberWithUnitsPrecise(buyItemPrice))
						.parse());
				anyOptionAvailable = true;
			} else {
				String loreLine = shopItemLore.get(1).second();
				if (loreLine != null) {
					result.add(loreLine);

				}
			}
		}

		if (sellItemPrice != 0) {
			if (sellItemPrice != -1) {
				result.add(shopItemLore.get(2).first()
						.parse("sellItemPrice", NumberUtils.formatNumberWithUnitsPrecise(sellItemPrice))
						.parse());
				anyOptionAvailable = true;
			} else {
				String loreLine = shopItemLore.get(2).second();
				if (loreLine != null) {
					result.add(loreLine);
				}
			}
		}

		if (anyOptionAvailable) {
			result.add(shopItemLore.get(3).first().parse());
		}

		return result;
	}

	public List<String> getDescriptionAsText() {
		if (description == null || description.length == 0) {
			return null;
		}
		return Arrays.stream(description).toList();
	}

	public String getName() {
		ItemStack guiItem = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(this.itemId())));
		String squaredName = guiItem.getDisplayName().getString();
		return squaredName.substring(1, squaredName.length() - 1);
	}
}

