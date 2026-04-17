package gg.mmorealms.module.breeding.backend.fabric.mixin;

import com.cobblemon.mod.common.block.PastureBlock;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.mixin_interfaces.IPastureContainer;
import gg.mmorealms.module.breeding.backend.fabric.utils.PastureUtils;
import gg.mmorealms.module.core.backend.common.utils.SoundUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PastureBlock.class)
public class PastureBlockMixin extends Block {

    public PastureBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("HEAD"), method = "useWithoutItem", cancellable = true)
    private void onUse(BlockState blockState,
                       Level level,
                       BlockPos blockPos,
                       Player player,
                       BlockHitResult hit,
                       CallbackInfoReturnable<InteractionResult> cir) {

        if (PastureUtils.isTopPart(blockState)) {
            return;
        }

        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        boolean allowWithFilledHand = config.pasture.canPickupEggWithFilledHand;

        if (!allowWithFilledHand) {
            boolean isHandEmpty = player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();

            if (!isHandEmpty) {
                cir.setReturnValue(InteractionResult.PASS);
                return;
            }
        }

        IPastureContainer pastureContainer = PastureUtils.getContainer(level, blockPos);

        if (!PastureUtils.hasEgg(pastureContainer)) {
            return;
        }

        player.getInventory().placeItemBackInInventory(pastureContainer.getItem(0));
        pastureContainer.removeItemNoUpdate(0);
        player.getInventory().tick();

        if (hasAnalogOutputSignal(blockState)) {
            level.updateNeighbourForOutputSignal(blockPos, blockState.getBlock());
        }

        SoundUtils.playSound(level, blockPos, SoundEvents.ITEM_PICKUP);

        cir.setReturnValue(InteractionResult.SUCCESS);
        cir.cancel();
    }

    /* ---------- Redstone ---------- */

    @Override
    protected boolean hasAnalogOutputSignal(@NotNull BlockState blockState) {
        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        if (PastureUtils.isTopPart(blockState)) {
            return false;
        }

        return config.pasture.hasComparatorSignal;
    }

    @Override
    protected int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos) {
        if (PastureUtils.isTopPart(blockState)) {
            return Redstone.SIGNAL_NONE;
        }

        IPastureContainer pastureContainer = PastureUtils.getContainer(level, blockPos);
        if (pastureContainer == null) {
            return Redstone.SIGNAL_NONE;
        }

        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        return pastureContainer.hasEgg()
                ? config.pasture.comparatorSignal
                : Redstone.SIGNAL_NONE;
    }
}

