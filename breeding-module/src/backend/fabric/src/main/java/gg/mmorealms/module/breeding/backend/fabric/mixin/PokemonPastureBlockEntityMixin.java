package gg.mmorealms.module.breeding.backend.fabric.mixin;

import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.math.Transformation;
import com.raduvoinea.utils.generic.RandomUtils;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.dto.BreedingPair;
import gg.mmorealms.module.breeding.backend.fabric.mixin_interfaces.IEggDisplay;
import gg.mmorealms.module.breeding.backend.fabric.mixin_interfaces.IPastureContainer;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingBlocks;
import gg.mmorealms.module.breeding.backend.fabric.utils.BreedingUtils;
import gg.mmorealms.module.breeding.backend.fabric.utils.PastureUtils;
import gg.mmorealms.module.core.backend.common.utils.SoundUtils;
import gg.mmorealms.module.core.common.utils.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Getter
@Mixin(PokemonPastureBlockEntity.class)
public abstract class PokemonPastureBlockEntityMixin
        extends BlockEntity
        implements IEggDisplay, IPastureContainer, WorldlyContainer {

    @Unique
    private final PokemonPastureBlockEntity self = (PokemonPastureBlockEntity) (Object) this;

    @Unique
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    @Unique
    @Setter
    private ElementHolder eggDisplayHolder;

    @Unique
    @Setter
    private HolderAttachment eggDisplayAttachment;

    @Unique
    @Setter
    private float breedingChanceBonus = 0f;

    @Unique
    @Setter
    private int breedingTickCounter = 0;

    public PokemonPastureBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @SuppressWarnings({"ConstantConditions"})
    @Inject(method = "TICKER$lambda$0", at = @At("HEAD"))
    private static void onTick(Level level, BlockPos blockPos, BlockState blockState, PokemonPastureBlockEntity blockEntity, CallbackInfo ci) {
        if (PastureUtils.isTopPart(blockState)) {
            return;
        }

        IPastureContainer pastureContainer = PastureUtils.getContainer(blockEntity);
        IEggDisplay eggDisplay = (IEggDisplay) (Object) blockEntity;

        // Handle egg display
        boolean shouldHaveDisplay = pastureContainer.hasEgg();
        boolean hasDisplay = eggDisplay.getEggDisplayHolder() != null
                && eggDisplay.getEggDisplayAttachment() != null;

        if (!shouldHaveDisplay && hasDisplay) {
            removeEggDisplay(eggDisplay);
        }

        if (shouldHaveDisplay && !hasDisplay) {
            createEggDisplay(eggDisplay, level, blockPos);
        }

        List<PokemonPastureBlockEntity.Tethering> tethered = blockEntity.getTetheredPokemon();
        if (tethered.isEmpty()) {
            return;
        }

        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        int attemptIntervalTick = TimeUtils.timeToTick(config.breeding.breedingAttemptInterval);

        // Time
        if (eggDisplay.getBreedingTickCounter() < attemptIntervalTick) {
            eggDisplay.incrementBreedingTickCounter();
            return;
        }
        eggDisplay.setBreedingTickCounter(0);

        if (pastureContainer.hasEgg()) {
            return;
        }

        List<Pokemon> pokemons = PastureUtils.getPokemons(tethered);
        List<BreedingPair> compatible = BreedingUtils.getCompatiblePairs(pokemons);
        if (compatible.isEmpty()) {
            return;
        }

        if (!shouldBreed(eggDisplay, compatible)) {
            eggDisplay.incrementBreedingChanceBonus();
            return;
        }
        eggDisplay.setBreedingChanceBonus(0f);

        // Item
        BreedingPair parents = RandomUtils.getRandom(compatible);

        ItemStack pokemonEggStack = parents.breedEgg();
        BreedingUtils.applyMirrorHerb(pokemons);
        pastureContainer.setItem(0, pokemonEggStack);

        level.updateNeighbourForOutputSignal(blockPos, blockState.getBlock());
        SoundUtils.playSound(level, blockPos, SoundEvents.CHICKEN_EGG);
    }

    @Unique
    private static void createEggDisplay(IEggDisplay eggDisplay, Level level, BlockPos blockPos) {
        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        eggDisplay.setEggDisplayHolder(new ElementHolder());

        BlockDisplayElement blockDisplay = new BlockDisplayElement();
        blockDisplay.setBlockState(BreedingBlocks.POKEMON_EGG.defaultBlockState());

        float scale = config.pasture.eggDisplayScale;
        Vec3 offset = config.pasture.eggDisplayOffset;

        Vec3 position = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())
                .add(offset);

        float centerOffset = (1.0f - scale) * 0.5f;

        blockDisplay.setTransformation(new Transformation(
                new Vector3f(centerOffset, 0, centerOffset),
                new Quaternionf(),
                new Vector3f(scale, scale, scale),
                new Quaternionf()
        ));

        eggDisplay.getEggDisplayHolder().addElement(blockDisplay);

        eggDisplay.setEggDisplayAttachment(ChunkAttachment.of(
                eggDisplay.getEggDisplayHolder(),
                (ServerLevel) level,
                position
        ));
    }

    @Unique
    private static boolean shouldBreed(IEggDisplay eggDisplay, List<BreedingPair> compatiblePairs) {
        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        List<Float> chances = config.breeding.chancePerPairCount;
        if (chances.isEmpty()) {
            return false;
        }

        int pairCount = compatiblePairs.size();

        int chanceIndex = Math.min(chances.size(), pairCount) - 1;
        float baseChance = chances.get(chanceIndex);
        float breedingChance = baseChance + eggDisplay.getBreedingChanceBonus();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        return breedingChance >= random.nextFloat(100);
    }

    @Unique
    private static void removeEggDisplay(IEggDisplay eggDisplay) {
        HolderAttachment eggDisplayAttachment = eggDisplay.getEggDisplayAttachment();
        ElementHolder eggDisplayHolder = eggDisplay.getEggDisplayHolder();

        if (eggDisplayAttachment != null) {
            eggDisplayAttachment.destroy();
            eggDisplay.setEggDisplayAttachment(null);
        }

        if (eggDisplayHolder != null) {
            eggDisplayHolder.destroy();
            eggDisplay.setEggDisplayHolder(null);
        }
    }

    @Unique
    private void dropEgg() {
        if (!this.hasEgg()) {
            return;
        }

        ItemStack eggStack = this.getItem(0);
        if (eggStack.isEmpty()) {
            return;
        }

        Level level = this.getLevel();
        if (level == null) {
            return;
        }

        BlockPos pos = this.getBlockPos();

        ItemEntity entity = new ItemEntity(level,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                eggStack.copy());
        level.addFreshEntity(entity);

        this.setItem(0, ItemStack.EMPTY);
    }

    @Inject(method = "onBroken", at = @At("HEAD"), remap = false)
    private void onBrokenDropEgg(CallbackInfo ci) {
        dropEgg();
        removeEggDisplay(this);

        if (level != null) {
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction direction) {
        return BreedingFabricModule.instance().getConfig().pasture.canHopperPickupEgg;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction direction) {
        return new int[]{0};
    }
}
