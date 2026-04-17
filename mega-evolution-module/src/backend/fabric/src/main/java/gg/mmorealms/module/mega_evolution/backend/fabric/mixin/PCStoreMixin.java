package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;

import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.mega_evolution.backend.fabric.utils.MegaEvolutionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PCStore.class, remap = false)
public class PCStoreMixin {
    @Inject(
            method = "set(Lcom/cobblemon/mod/common/api/storage/pc/PCPosition;Lcom/cobblemon/mod/common/pokemon/Pokemon;)V",
            at = @At("HEAD")
    )
    private void onSet(PCPosition position, Pokemon pokemon, CallbackInfo ci) {
        mega_evolution$onSet(pokemon);
    }

    @Unique
    private void mega_evolution$onSet(Pokemon pokemon) {
        if (pokemon == null) {
            return;
        }

        if (MegaEvolutionUtils.isMegaPokemon(pokemon)) {
            MegaEvolutionUtils.megaDevolve(pokemon);
        }
    }
}
