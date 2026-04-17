package gg.mmorealms.module.mega_evolution.backend.fabric.registry;

import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import gg.mmorealms.module.mega_evolution.backend.fabric.blocks.MegaEvolutionDeepslateOreBlock;
import gg.mmorealms.module.mega_evolution.backend.fabric.blocks.MegaEvolutionStoneOreBlock;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import net.minecraft.world.level.block.Block;

import java.util.Map;



public class MegaEvolutionBlocks {

    private MegaEvolutionBlocks() { }

    public static Map<MegaEvolution, Block> MEGA_STONE_ORES = MegaEvolution.linkedMapWithGems(MegaEvolutionBlocks::registeMegaStoneOreBlock);

    public static Map<MegaEvolution, Block> MEGA_DEEPSLATE_ORES = MegaEvolution.linkedMapWithGems(MegaEvolutionBlocks::registeMegaDeepslateOreBlock);

    public static void register() { }

    private static Block registeMegaStoneOreBlock(MegaEvolution evolution) {
        return PolymerRegistryUtils.registerBlock(evolution.getStoneOreName(), new MegaEvolutionStoneOreBlock(evolution));
    }

    private static Block registeMegaDeepslateOreBlock(MegaEvolution evolution) {
        return PolymerRegistryUtils.registerBlock(evolution.getDeepslateOreName(), new MegaEvolutionDeepslateOreBlock(evolution));
    }

}
