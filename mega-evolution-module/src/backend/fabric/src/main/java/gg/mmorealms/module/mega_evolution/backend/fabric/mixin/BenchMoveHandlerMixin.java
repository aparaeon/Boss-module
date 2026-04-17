package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.net.messages.server.BenchMovePacket;
import com.cobblemon.mod.common.net.serverhandling.storage.BenchMoveHandler;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.llamalad7.mixinextras.sugar.Local;
import gg.mmorealms.module.mega_evolution.backend.fabric.utils.MegaEvolutionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;


@Mixin(BenchMoveHandler.class)
public class BenchMoveHandlerMixin {

    @Inject(
            method = "handle(Lcom/cobblemon/mod/common/net/messages/server/BenchMovePacket;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;exchangeMove(Lcom/cobblemon/mod/common/api/moves/MoveTemplate;Lcom/cobblemon/mod/common/api/moves/MoveTemplate;)Z"
            )
    )
    private void megaDevolveOnRequiredMoveRemoved(BenchMovePacket packet, MinecraftServer server, ServerPlayer player, CallbackInfo ci, @Local Pokemon pokemon) {
        mega_evolution$megaDevolveOnRequiredMoveRemoved(packet, pokemon);
    }

    @Unique
    private void mega_evolution$megaDevolveOnRequiredMoveRemoved(BenchMovePacket packet, Pokemon pokemon) {
        Map<FormData, String> formToMove = MegaEvolutionUtils.getFormToRequiredMegaMove(pokemon);

        if (formToMove.isEmpty()) {
            return;
        }

        FormData formData = pokemon.getForm();

        String requiredMoveName = formToMove.get(formData);
        if (requiredMoveName == null) {
            return;
        }

        MoveTemplate oldMove = packet.getOldMove();
        if (oldMove == null) {
            return;
        }

        String oldMoveName = oldMove.getName();

        if (oldMoveName.equals(requiredMoveName)) {
            MegaEvolutionUtils.megaDevolve(pokemon);
        }
    }

}
