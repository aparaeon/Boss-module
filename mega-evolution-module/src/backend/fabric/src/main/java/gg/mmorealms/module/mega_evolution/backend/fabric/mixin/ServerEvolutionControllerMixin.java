package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;

import com.cobblemon.mod.common.api.pokemon.evolution.Evolution;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.evolution.controller.ServerEvolutionController;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.mega_evolution.backend.fabric.MegaEvolutionFabricModule;
import gg.mmorealms.module.mega_evolution.backend.fabric.config.MegaEvolutionConfig;
import gg.mmorealms.module.mega_evolution.backend.fabric.interfaces.IMegaPokemonEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerEvolutionController.class)
public class ServerEvolutionControllerMixin {

    @Unique
    private final ServerEvolutionController self = (ServerEvolutionController) (Object) this;

    @Inject(
            method = "start(Lcom/cobblemon/mod/common/api/pokemon/evolution/Evolution;)V",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void preventEvolution(Evolution evolution, CallbackInfo ci) {
        mega_evolution$preventEvolution(ci);
    }

    @Unique
    private void mega_evolution$preventEvolution(CallbackInfo ci) {
        Pokemon pokemon = self.pokemon();
        PokemonEntity pokemonEntity = pokemon.getEntity();
        if (pokemonEntity == null) {
            return;
        }

        if (pokemonEntity instanceof IMegaPokemonEntity megaPokemon && megaPokemon.isMegaEvolving()) {
            ServerPlayer player = pokemon.getOwnerPlayer();

            if (player != null) {
                MegaEvolutionConfig config = MegaEvolutionFabricModule.instance().getConfig();

                User user = User.get(player);
                user.sendActionMessage(config.lang.cantEvolveMessage);
            }
            ci.cancel();
        }
    }

}
