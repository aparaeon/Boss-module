package gg.mmorealms.module.core.backend.fabric.registry;

import com.mojang.serialization.Codec;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import net.minecraft.core.component.DataComponentType;


public class CoreDataComponents {

	private CoreDataComponents() { }

	public static DataComponentType<Boolean> NO_RENAME = PolymerRegistryUtils.registerDataComponentType(CoreDataKeys.NO_RENAME_KEY, Codec.BOOL);

}
