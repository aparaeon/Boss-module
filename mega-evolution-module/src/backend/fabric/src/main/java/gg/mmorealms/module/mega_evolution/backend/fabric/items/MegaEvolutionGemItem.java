package gg.mmorealms.module.mega_evolution.backend.fabric.items;

import gg.mmorealms.module.core.backend.fabric.items.PolymerNamedItem;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import net.minecraft.world.item.Items;

public class MegaEvolutionGemItem extends PolymerNamedItem {

    public MegaEvolutionGemItem(MegaEvolution evolution) {
        super(evolution.getGemDisplayName(),
                evolution.getGemName(),
                Items.DIAMOND,
                new Properties().stacksTo(1));
    }

}
