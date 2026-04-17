package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;

import com.cobblemon.mod.common.net.messages.server.SendOutPokemonPacket;
import com.cobblemon.mod.common.net.serverhandling.storage.SendOutPokemonHandler;
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

@Mixin(SendOutPokemonHandler.class)
public class SendOutPokemonHandlerMixin {

    @Inject(
            method = "handle(Lcom/cobblemon/mod/common/net/messages/server/SendOutPokemonPacket;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventCallback(SendOutPokemonPacket packet, MinecraftServer server, ServerPlayer player, CallbackInfo ci) {
        mega_evolution$preventCallback(packet, player, ci);
    }

    @Unique
    private void mega_evolution$preventCallback(SendOutPokemonPacket packet, ServerPlayer player, CallbackInfo ci) {
        int slot = packet.getSlot();
        if (slot < 0) {
            return;
        }

        if (MegaEvolutionUtils.isMegaEvolving(player, slot)) {
            MegaEvolutionConfig config = MegaEvolutionFabricModule.instance().getConfig();

            User user = User.get(player);
            user.sendActionMessage(config.lang.cantCallbackMessage);

            ci.cancel();
        }
    }

}
