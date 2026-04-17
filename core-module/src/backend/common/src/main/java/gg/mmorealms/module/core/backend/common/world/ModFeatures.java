package gg.mmorealms.module.core.backend.common.world;

import gg.mmorealms.module.core.backend.common.utils.ResourceUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;


public class ModFeatures {
    public static final Feature<SingleOreConfiguration> SINGLE_ORE = new SingleOreFeature(SingleOreConfiguration.CODEC);

    public static void register() {
        Registry.register(BuiltInRegistries.FEATURE, ResourceUtils.modResource("single_ore"), SINGLE_ORE);
    }
}
