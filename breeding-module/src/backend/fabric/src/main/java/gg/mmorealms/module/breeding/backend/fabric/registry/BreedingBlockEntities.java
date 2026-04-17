package gg.mmorealms.module.breeding.backend.fabric.registry;

import gg.mmorealms.module.breeding.backend.fabric.blocks.PokemonEggBlockEntity;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;



public class BreedingBlockEntities {

    public static BlockEntityType<PokemonEggBlockEntity> POKEMON_EGG = PolymerRegistryUtils.registerBlockEntity(BreedingDataKeys.POKEMON_EGG_KEY, PokemonEggBlockEntity::new, BreedingBlocks.POKEMON_EGG);

    public static void register() {
    }

}