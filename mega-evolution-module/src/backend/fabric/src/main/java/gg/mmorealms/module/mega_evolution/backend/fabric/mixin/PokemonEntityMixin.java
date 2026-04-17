package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.mega_evolution.backend.fabric.interfaces.IMegaPokemonEntity;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Getter
@Mixin(PokemonEntity.class)
public class PokemonEntityMixin implements IMegaPokemonEntity {

    @Unique
    private final PokemonEntity self = (PokemonEntity) (Object) this;

    @Unique
    private static final String MEGA_EVOLUTION_LOCK = "mega_evolving";

    @Unique
    private static final String MEGA_EVOLUTION_LOCK_NBT_KEY = "mega_evolving";

    @Unique
    private boolean isMegaEvolving = false;

    @Override
    public void mega_evolution$addMegaEvolutionLock() {
        mega_evolution$setMegaEvolutionLock(true);
    }

    @Override
    public void mega_evolution$removeMegaEvolutionLock() {
        mega_evolution$setMegaEvolutionLock(false);
    }

    @Override
    public void mega_evolution$setMegaEvolutionLock(boolean locked) {
        if (isMegaEvolving != locked) {
            isMegaEvolving = locked;

            // Update busy locks immediately
            if (locked) {
                self.getBusyLocks().remove(MEGA_EVOLUTION_LOCK);
                self.getBusyLocks().add(MEGA_EVOLUTION_LOCK);
            } else {
                self.getBusyLocks().remove(MEGA_EVOLUTION_LOCK);
            }
        }
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void saveCustomLockData(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        mega_evolution$saveCustomLockData(nbt);
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void loadCustomLockData(CompoundTag nbt, CallbackInfo ci) {
        mega_evolution$loadCustomLockData(nbt);
    }

    @Unique
    private void mega_evolution$saveCustomLockData(CompoundTag nbt) {
        if (isMegaEvolving) {
            nbt.putBoolean(MEGA_EVOLUTION_LOCK_NBT_KEY, true);
        }
    }

    @Unique
    private void mega_evolution$loadCustomLockData(CompoundTag nbt) {
        if (nbt.contains(MEGA_EVOLUTION_LOCK_NBT_KEY)) {
            mega_evolution$setMegaEvolutionLock(nbt.getBoolean(MEGA_EVOLUTION_LOCK_NBT_KEY));
        }
    }
}