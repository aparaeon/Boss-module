package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;

import com.cobblemon.mod.common.net.messages.server.BattleChallengePacket;
import com.cobblemon.mod.common.net.serverhandling.ChallengeHandler;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.mega_evolution.backend.fabric.MegaEvolutionFabricModule;
import gg.mmorealms.module.mega_evolution.backend.fabric.config.MegaEvolutionConfig;
import gg.mmorealms.module.mega_evolution.backend.fabric.utils.MegaEvolutionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;


@Mixin(ChallengeHandler.class)
public class ChallengeHandlerMixin {

    @Inject(
            method = "handle(Lcom/cobblemon/mod/common/net/messages/server/BattleChallengePacket;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventChallengeIfMegaEvolving(BattleChallengePacket packet,
                                                MinecraftServer server,
                                                ServerPlayer player,
                                                CallbackInfo ci) {

        mega_evolution$preventChallengeIfMegaEvolving(packet, player, ci);
    }

    @Unique
    private void mega_evolution$preventChallengeIfMegaEvolving(BattleChallengePacket packet,
                                                               ServerPlayer player,
                                                               CallbackInfo ci) {

        UUID pokemonUUID = packet.getSelectedPokemonId();

        if (MegaEvolutionUtils.isMegaEvolving(player, pokemonUUID)) {
            MegaEvolutionConfig config = MegaEvolutionFabricModule.instance().getConfig();

            User user = User.get(player);
            user.sendActionMessage(config.lang.cantChallengeMessage);

            ci.cancel();
        }
    }

}
