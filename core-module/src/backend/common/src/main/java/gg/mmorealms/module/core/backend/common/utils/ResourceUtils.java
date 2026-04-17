package gg.mmorealms.module.core.backend.common.utils;

import net.minecraft.resources.ResourceLocation;

public class ResourceUtils {

	private ResourceUtils() {
	}

	public static ResourceLocation modResource(String path) {
		return ResourceLocation.fromNamespaceAndPath("mmorealms", path);
	}

}
