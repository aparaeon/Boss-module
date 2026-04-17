package gg.mmorealms.module.core.backend.common.utils;

import com.mojang.serialization.Codec;
import com.raduvoinea.utils.logger.Logger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import java.util.function.Supplier;

public class CompoundTagUtils {

	private CompoundTagUtils() {
	}

	public static <T> void saveWithCodec(CompoundTag compoundTag, Codec<T> codec, String key, T data) {
		codec.encodeStart(NbtOps.INSTANCE, data)
				.resultOrPartial(error -> Logger.error("Failed to encode " + key + " to NBT: " + error))
				.ifPresent(nbt -> compoundTag.put(key, nbt));
	}

	public static <T> T loadWithCodec(CompoundTag compoundTag, Codec<T> codec, String key, T defaultValue) {
		if (!compoundTag.contains(key)) {
			return defaultValue;
		}

		return codec.parse(NbtOps.INSTANCE, compoundTag.get(key))
				.resultOrPartial(error -> Logger.error("Failed to decode " + key + "from NBT: " + error))
				.orElse(defaultValue);
	}


	public static <T> T loadWithCodec(CompoundTag compoundTag, Codec<T> codec, String key, Supplier<T> defaultSupplier) {
		if (!compoundTag.contains(key)) {
			return defaultSupplier.get();
		}

		return codec.parse(NbtOps.INSTANCE, compoundTag.get(key))
				.resultOrPartial(error -> Logger.error("Failed to decode " + key + "from NBT: " + error))
				.orElseGet(defaultSupplier);
	}

}
