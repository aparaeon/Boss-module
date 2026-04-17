package gg.mmorealms.module.breeding.backend.fabric.blocks;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingBlockStates;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingProperties;
import gg.mmorealms.module.breeding.backend.fabric.utils.PokemonEggUtils;
import gg.mmorealms.module.core.backend.common.utils.SoundUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;



public class PokemonEggBlock extends BaseEntityBlock implements PolymerTexturedBlock {

    public PokemonEggBlock() {
        this(Properties.of()
                .destroyTime(1f)
                .pushReaction(PushReaction.DESTROY)
                .sound(SoundType.STONE));
    }

    protected PokemonEggBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any()
                .setValue(BreedingProperties.AVERAGE_IVS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BreedingProperties.AVERAGE_IVS);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(PokemonEggBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new PokemonEggBlockEntity(blockPos, blockState);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return BreedingBlockStates.POKEMON_EGG;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader levelReader,
                                                @NotNull BlockPos blockPos,
                                                @NotNull BlockState blockState) {

        ItemStack itemStack = PokemonEggUtils.pokemonEggItemStack();

        BlockEntity blockEntity = levelReader.getBlockEntity(blockPos);
        if (blockEntity instanceof PokemonEggBlockEntity pokemonEggBlockEntity) {
            pokemonEggBlockEntity.saveToItem(itemStack, levelReader.registryAccess());
        }

        return itemStack;
    }

    /* ---------- Interaction ---------- */

    @Override
    public void setPlacedBy(Level level,
                            @NotNull BlockPos blockPos,
                            @NotNull BlockState blockState,
                            @Nullable LivingEntity placer,
                            @NotNull ItemStack itemStack) {

        BlockEntity blockEntity = level.getBlockEntity(blockPos);

        if (blockEntity instanceof PokemonEggBlockEntity pokemonEggBlockEntity) {
            pokemonEggBlockEntity.loadFromItem(itemStack);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        int averageIVs = PokemonEggUtils.getAverageIVs(blockPlaceContext.getItemInHand());
        return defaultBlockState().setValue(BreedingProperties.AVERAGE_IVS, averageIVs);
    }

    public @NotNull ItemStack pickupBlock(@Nullable Player player,
                                          LevelAccessor levelAccessor,
                                          BlockPos blockPos,
                                          BlockState blockState) {

        BlockEntity blockEntity = levelAccessor.getBlockEntity(blockPos);

        if (blockEntity instanceof PokemonEggBlockEntity pokemonEggBlockEntity) {
            ItemStack eggItemStack = PokemonEggUtils.pokemonEggItemStack();
            pokemonEggBlockEntity.saveToItem(eggItemStack, levelAccessor.registryAccess());

            levelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            SoundUtils.playSound(levelAccessor, blockPos, SoundEvents.ITEM_PICKUP);

            return eggItemStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState blockState,
                                                        @NotNull Level level,
                                                        @NotNull BlockPos blockPos,
                                                        @NotNull Player player,
                                                        @NotNull BlockHitResult blockHitResult) {

        ItemStack itemStack = this.pickupBlock(player, level, blockPos, blockState);
        player.addItem(itemStack);

        if (itemStack == ItemStack.EMPTY) {
            return InteractionResult.PASS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onPlace(@NotNull BlockState newBlockState,
                           @NotNull Level level,
                           @NotNull BlockPos blockPos,
                           @NotNull BlockState oldBlockState,
                           boolean movedByPiston) {

        SoundUtils.playPlaceSound(level, blockPos, newBlockState);
    }

    /* ---------- Destroy ---------- */

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return level.getFluidState(pos).isEmpty();
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState blockState, LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof PokemonEggBlockEntity pokemonEggBlockEntity) {
            ItemStack itemStack = PokemonEggUtils.pokemonEggItemStack();
            pokemonEggBlockEntity.saveToItem(itemStack, builder.getLevel().registryAccess());
            return List.of(itemStack);
        }

        return List.of(PokemonEggUtils.pokemonEggItemStack());
    }

    @Override
    public void destroy(@NotNull LevelAccessor levelAccessor, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        SoundUtils.playBreakSound(levelAccessor, blockPos, blockState);
    }

    /* ---------- Redstone ---------- */

    @Override
    protected boolean hasAnalogOutputSignal(@NotNull BlockState blockState) {
        return BreedingFabricModule.instance().getConfig().egg.hasComparatorSignal;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos) {
        int averageIVs = blockState.getValue(BreedingProperties.AVERAGE_IVS);
        return Math.floorDiv(averageIVs * Redstone.SIGNAL_MAX, 31);
    }
}
