package gg.mmorealms.module.breeding.backend.fabric.registry;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.items.PokemonEggItem;
import gg.mmorealms.module.core.backend.common.utils.ResourceUtils;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


public class BreedingItems {

    private BreedingItems() {
    }

    public static final Item POKEMON_EGG = PolymerRegistryUtils.registerItem(BreedingDataKeys.POKEMON_EGG_KEY, new PokemonEggItem());

    public static final CreativeModeTab ITEM_GROUP = CreativeModeTab.builder(null, -1)
            .title(Component.literal("Breeding"))
            .icon(BreedingItems.POKEMON_EGG::getDefaultInstance)
            .displayItems((ctx, output) -> {
                output.accept(POKEMON_EGG);
            })
            .build();

    public static void register() {
        PolymerItemGroupUtils.registerPolymerItemGroup(ResourceUtils.modResource("breeding_items"), ITEM_GROUP);
        registerDispensable();
    }

    private static void registerDispensable() {
        DispenserBlock.registerBehavior(POKEMON_EGG, new DispenseItemBehavior() {
            @Override
            public @NotNull ItemStack dispense(@NotNull BlockSource blockSource, @NotNull ItemStack itemStack) {
                BreedingConfig config = BreedingFabricModule.instance().getConfig();

                if (!config.egg.isPlaceable || !config.egg.isDispensable) {
                    return itemStack;
                }

                Level level = blockSource.level();
                BlockEntity dispenserEntity = blockSource.blockEntity();
                Direction facing = dispenserEntity.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos targetPos = dispenserEntity.getBlockPos();

                Vec3 hitVec = new Vec3(
                        targetPos.getX(),
                        targetPos.getY(),
                        targetPos.getZ());

                BlockHitResult hit = new BlockHitResult(hitVec, facing, targetPos, false);

                UseOnContext ctx = new UseOnContext(level, null,
                        InteractionHand.MAIN_HAND,
                        itemStack, hit);

                InteractionResult result = POKEMON_EGG.useOn(ctx);
                if (result.consumesAction()) {
                    itemStack.shrink(1);
                }

                return itemStack;
            }
        });
    }

}
