package gg.mmorealms.module.breeding.backend.fabric.registry;

import eu.pb4.polymer.blocks.api.BlockModelType;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import net.minecraft.world.level.block.state.BlockState;


public class BreedingBlockStates {

    private BreedingBlockStates() { }

    public static BlockState POKEMON_EGG = PolymerRegistryUtils.registerBlockState(BreedingDataKeys.POKEMON_EGG_KEY, BlockModelType.CACTUS_BLOCK); // Cactus has the closest hitbox

    public static void register() {
    }

}

