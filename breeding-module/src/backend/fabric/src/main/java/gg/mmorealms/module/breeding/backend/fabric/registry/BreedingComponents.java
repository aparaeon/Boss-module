package gg.mmorealms.module.breeding.backend.fabric.registry;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.mojang.serialization.Codec;
import gg.mmorealms.module.breeding.backend.fabric.dto.ParentData;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import net.minecraft.core.component.DataComponentType;



public class BreedingComponents {

    private BreedingComponents() { }

    public static final DataComponentType<Float> STEPS = PolymerRegistryUtils.registerDataComponentType(BreedingDataKeys.STEPS_KEY, Codec.FLOAT);

    public static final DataComponentType<Float> STEPS_GOAL = PolymerRegistryUtils.registerDataComponentType(BreedingDataKeys.STEPS_GOAL_KEY, Codec.FLOAT);

    public static final DataComponentType<ParentData> FATHER = PolymerRegistryUtils.registerDataComponentType(BreedingDataKeys.FATHER_KEY, ParentData.CODEC);

    public static final DataComponentType<ParentData> MOTHER = PolymerRegistryUtils.registerDataComponentType(BreedingDataKeys.MOTHER_KEY, ParentData.CODEC);

    public static final DataComponentType<PokemonProperties> POKEMON_PROPERTIES = PolymerRegistryUtils.registerDataComponentType(BreedingDataKeys.POKEMON_PROPERTIES_KEY, PokemonProperties.getCODEC());

}