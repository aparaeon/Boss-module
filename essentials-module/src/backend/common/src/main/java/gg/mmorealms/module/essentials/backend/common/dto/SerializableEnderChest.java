package gg.mmorealms.module.essentials.backend.common.dto;

import com.google.gson.JsonElement;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@NoArgsConstructor
public class SerializableEnderChest {

	public List<JsonElement> items;

	public SerializableEnderChest(List<ItemStack> items) {
		this.items = CodecUtils.serialize(ItemStack.CODEC, items);
	}

	public List<ItemStack> getItems() {
		return CodecUtils.deserialize(ItemStack.CODEC, items, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

}
