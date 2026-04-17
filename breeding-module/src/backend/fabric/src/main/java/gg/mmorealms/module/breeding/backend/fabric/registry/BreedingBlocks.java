package gg.mmorealms.module.breeding.backend.fabric.registry;

import gg.mmorealms.module.breeding.backend.fabric.blocks.PokemonEggBlock;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import net.minecraft.world.level.block.Block;


public class BreedingBlocks {

    private BreedingBlocks() { }

    public static Block POKEMON_EGG = PolymerRegistryUtils.registerBlock(BreedingDataKeys.POKEMON_EGG_KEY, new PokemonEggBlock());

    public static void register() {
    }

}
