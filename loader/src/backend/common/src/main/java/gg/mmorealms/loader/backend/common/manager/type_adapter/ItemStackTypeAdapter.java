package gg.mmorealms.loader.backend.common.manager.type_adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.raduvoinea.utils.file_manager.dto.GsonTypeAdapter;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

public class ItemStackTypeAdapter extends GsonTypeAdapter<ItemStack> {

	public ItemStackTypeAdapter(ClassLoader classLoader) {
		super(classLoader, ItemStack.class);
	}

	@Override
	public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
		return CodecUtils.deserialize(ItemStack.CODEC, jsonElement, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

	@Override
	public JsonElement serialize(ItemStack src, Type type, JsonSerializationContext context) {
		return CodecUtils.serialize(ItemStack.CODEC, src);
	}

}
