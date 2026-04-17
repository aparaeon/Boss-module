package gg.mmorealms.module.mega_evolution.backend.fabric.registry;

import gg.mmorealms.module.core.backend.common.utils.ResourceUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MegaEvolutionTags {

    private MegaEvolutionTags() { }

    public static final TagKey<Item> MEGA_GEM =
            TagKey.create(Registries.ITEM, ResourceUtils.modResource("mega_gem"));

}
