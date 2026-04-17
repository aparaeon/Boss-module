package gg.mmorealms.module.essentials.backend.common.dto;

import com.google.gson.JsonElement;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@NoArgsConstructor
public class SerializablePlayerInventory {

	public List<JsonElement> items;
	public List<JsonElement> hotbar;
	public List<JsonElement> armor;
	public List<JsonElement> offhand;

	public SerializablePlayerInventory(List<ItemStack> items, List<ItemStack> hotbar, List<ItemStack> armor, List<ItemStack> offhand) {
		this.items = CodecUtils.serialize(ItemStack.CODEC, items);
		this.hotbar = CodecUtils.serialize(ItemStack.CODEC, hotbar);
		this.armor = CodecUtils.serialize(ItemStack.CODEC, armor).reversed();
		this.offhand = CodecUtils.serialize(ItemStack.CODEC, offhand);
	}

	public List<ItemStack> getItems() {
		return CodecUtils.deserialize(ItemStack.CODEC, items, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

	public List<ItemStack> getHotbar() {
		return CodecUtils.deserialize(ItemStack.CODEC, hotbar, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

	public List<ItemStack> getArmor() {
		return CodecUtils.deserialize(ItemStack.CODEC, armor, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

	public List<ItemStack> getOffhand() {
		return CodecUtils.deserialize(ItemStack.CODEC, offhand, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

}
