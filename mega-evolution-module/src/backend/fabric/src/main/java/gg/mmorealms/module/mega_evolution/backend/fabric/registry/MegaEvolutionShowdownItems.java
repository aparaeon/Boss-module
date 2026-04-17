package gg.mmorealms.module.mega_evolution.backend.fabric.registry;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;

public class MegaEvolutionShowdownItems {

    private MegaEvolutionShowdownItems() { }

    public static void register() {
        remapMegaGems();
    }

    private static void remapMegaGems() {
        MegaEvolutionItems.MEGA_GEMS.forEach((evolution, item) -> {
            CobblemonHeldItemManager.INSTANCE.registerRemap(item, evolution.getGemName());
        });
    }

}