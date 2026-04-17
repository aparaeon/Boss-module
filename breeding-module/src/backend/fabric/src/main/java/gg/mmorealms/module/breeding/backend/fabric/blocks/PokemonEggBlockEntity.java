package gg.mmorealms.module.breeding.backend.fabric.blocks;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import gg.mmorealms.module.breeding.backend.fabric.dto.ParentData;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingBlockEntities;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingComponents;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingDataKeys;
import gg.mmorealms.module.core.backend.common.utils.CompoundTagUtils;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


@Getter
public class PokemonEggBlockEntity extends BlockEntity {

    private float steps = 0f;
    private float stepsGoal = 0f;
    private ParentData father = new ParentData();
    private ParentData mother = new ParentData();
    private PokemonProperties pokemonProperties = new PokemonProperties();

    public PokemonEggBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(BreedingBlockEntities.POKEMON_EGG, blockPos, blockState);
    }

    public PokemonEggBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        compoundTag.putFloat(BreedingDataKeys.STEPS_KEY, steps);
        compoundTag.putFloat(BreedingDataKeys.STEPS_GOAL_KEY, stepsGoal);

        CompoundTagUtils.saveWithCodec(compoundTag, ParentData.CODEC, BreedingDataKeys.FATHER_KEY, father);
        CompoundTagUtils.saveWithCodec(compoundTag, ParentData.CODEC, BreedingDataKeys.MOTHER_KEY, mother);
        CompoundTagUtils.saveWithCodec(compoundTag, PokemonProperties.getCODEC(), BreedingDataKeys.POKEMON_PROPERTIES_KEY, pokemonProperties);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        steps = compoundTag.getFloat(BreedingDataKeys.STEPS_KEY);
        stepsGoal = compoundTag.getFloat(BreedingDataKeys.STEPS_GOAL_KEY);

        father = CompoundTagUtils.loadWithCodec(compoundTag, ParentData.CODEC, BreedingDataKeys.FATHER_KEY, new ParentData());
        mother = CompoundTagUtils.loadWithCodec(compoundTag, ParentData.CODEC, BreedingDataKeys.MOTHER_KEY, new ParentData());
        pokemonProperties = CompoundTagUtils.loadWithCodec(compoundTag,
                PokemonProperties.getCODEC(),
                BreedingDataKeys.POKEMON_PROPERTIES_KEY,
                new PokemonProperties());
    }

    @Override
    public void saveToItem(ItemStack itemStack, @NotNull HolderLookup.Provider provider) {
        itemStack.set(BreedingComponents.STEPS, steps);
        itemStack.set(BreedingComponents.STEPS_GOAL, stepsGoal);
        itemStack.set(BreedingComponents.FATHER, father);
        itemStack.set(BreedingComponents.MOTHER, mother);
        itemStack.set(BreedingComponents.POKEMON_PROPERTIES, pokemonProperties);
    }

    public void loadFromItem(ItemStack itemStack) {
        this.steps = itemStack.getOrDefault(BreedingComponents.STEPS, 0f);
        this.stepsGoal = itemStack.getOrDefault(BreedingComponents.STEPS_GOAL, 0f);
        this.father = itemStack.getOrDefault(BreedingComponents.FATHER, new ParentData());
        this.mother = itemStack.getOrDefault(BreedingComponents.MOTHER, new ParentData());
        this.pokemonProperties = itemStack.getOrDefault(BreedingComponents.POKEMON_PROPERTIES, new PokemonProperties());
        this.setChanged();
    }

}
