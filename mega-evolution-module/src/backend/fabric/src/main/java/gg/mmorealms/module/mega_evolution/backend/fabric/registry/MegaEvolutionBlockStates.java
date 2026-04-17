package gg.mmorealms.module.mega_evolution.backend.fabric.registry;

import eu.pb4.polymer.blocks.api.BlockModelType;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;


public class MegaEvolutionBlockStates {

    private MegaEvolutionBlockStates() { }

    public static Map<MegaEvolution, BlockState> MEGA_STONE_ORE_BLOCK_STATES = MegaEvolution.linkedMapWithGems(MegaEvolutionBlockStates::registeMegaStoneOreBlockState);

    public static Map<MegaEvolution, BlockState> MEGA_DEEPSLATE_ORE_BLOCK_STATES = MegaEvolution.linkedMapWithGems(MegaEvolutionBlockStates::registeMegaDeepslateOreBlockState);

    public static void register() { }

    private static BlockState registeMegaStoneOreBlockState(MegaEvolution evolution) {
        return PolymerRegistryUtils.registerBlockState(evolution.getStoneOreName(), BlockModelType.FULL_BLOCK);
    }

    private static BlockState registeMegaDeepslateOreBlockState(MegaEvolution evolution) {
        return PolymerRegistryUtils.registerBlockState(evolution.getDeepslateOreName(), BlockModelType.FULL_BLOCK);
    }

}
