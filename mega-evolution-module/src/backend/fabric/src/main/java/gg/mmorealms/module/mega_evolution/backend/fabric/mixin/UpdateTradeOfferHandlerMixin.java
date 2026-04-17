package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PartyPosition;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.net.messages.server.trade.UpdateTradeOfferPacket;
import com.cobblemon.mod.common.net.serverhandling.trade.UpdateTradeOfferHandler;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.mega_evolution.backend.fabric.MegaEvolutionFabricModule;
import gg.mmorealms.module.mega_evolution.backend.fabric.utils.MegaEvolutionUtils;
import kotlin.Pair;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;


@Mixin(UpdateTradeOfferHandler.class)
public class UpdateTradeOfferHandlerMixin {

    @Inject(
            method = "handle(Lcom/cobblemon/mod/common/net/messages/server/trade/UpdateTradeOfferPacket;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cobblemon/mod/common/net/messages/server/trade/UpdateTradeOfferPacket;getNewOffer()Lkotlin/Pair;"
            ),
            cancellable = true
    )
    private void cancelMegaTrade(UpdateTradeOfferPacket packet, MinecraftServer server, ServerPlayer player, CallbackInfo ci) {
        mega_evolution$cancelMegaTrade(packet, player, ci);
    }

    @Unique
    private void mega_evolution$cancelMegaTrade(UpdateTradeOfferPacket packet, ServerPlayer player, CallbackInfo ci) {
        Pair<UUID, PartyPosition> newOffer = packet.getNewOffer();
        if (newOffer == null) {
            return;
        }

        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
        Pokemon pokemon = party.get(newOffer.getSecond());
        if (pokemon == null) {
            return;
        }

        if (MegaEvolutionUtils.isMegaPokemon(pokemon) || MegaEvolutionUtils.isMegaEvolving(pokemon)) {
            User user = User.get(player);

            MessageBuilder messageBuilder = MegaEvolutionFabricModule.instance().getConfig().lang.cantTradeMessage;
            user.sendMessage(messageBuilder);

            ci.cancel();
        }
    }

}
