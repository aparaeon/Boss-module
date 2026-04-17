package gg.mmorealms.module.breeding.backend.fabric.utils;

import com.cobblemon.mod.common.block.PastureBlock;
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.breeding.backend.fabric.mixin_interfaces.IPastureContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PastureUtils {

    private PastureUtils() {
    }

    public static Pokemon breedRandomPair(PokemonPastureBlockEntity blockEntity) {
        List<Pokemon> pokemons = getPokemons(blockEntity);
        return BreedingUtils.breedRandomPair(pokemons);
    }

    public static boolean isTopPart(BlockState blockState) {
        return isPart(blockState, PastureBlock.PasturePart.TOP);
    }

    public static boolean isBottomPart(BlockState blockState) {
        return isPart(blockState, PastureBlock.PasturePart.BOTTOM);
    }

    private static boolean isPart(BlockState blockState, PastureBlock.PasturePart pasturePart) {
        EnumProperty<PastureBlock.PasturePart> partProperty = PastureBlock.Companion.getPART();

        if (blockState.hasProperty(partProperty)) {
            return blockState.getValue(partProperty) == pasturePart;
        } else {
            return false;
        }
    }

    public static boolean hasEgg(Level level, BlockPos blockPos) {
        return hasEgg(getContainer(level, blockPos));
    }

    public static boolean hasEgg(BlockEntity blockEntity) {
        return hasEgg(getContainer(blockEntity));
    }

    public static boolean hasEgg(@Nullable IPastureContainer pastureContainer) {
        if (pastureContainer == null) {
            return false;
        }

        return pastureContainer.hasEgg();
    }

    @Nullable
    public static IPastureContainer getContainer(Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        return getContainer(blockEntity);
    }

    @Nullable
    public static IPastureContainer getContainer(BlockEntity blockEntity) {
        if (blockEntity instanceof IPastureContainer container) {
            return container;
        }

        return null;
    }

    public static List<Pokemon> getPokemons(PokemonPastureBlockEntity blockEntity) {
        return getPokemons(blockEntity.getTetheredPokemon());
    }

    public static List<Pokemon> getPokemons(List<PokemonPastureBlockEntity.Tethering> tethered) {
        return tethered.stream()
                .map(PokemonPastureBlockEntity.Tethering::getPokemon)
                .filter(Objects::nonNull)
                .toList();
    }

}
