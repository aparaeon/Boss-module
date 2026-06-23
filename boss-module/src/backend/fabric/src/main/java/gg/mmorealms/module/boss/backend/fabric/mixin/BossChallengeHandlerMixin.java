package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.server.BattleChallengePacket;
import com.cobblemon.mod.common.net.serverhandling.ChallengeHandler;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.NbtKeys;
import gg.mmorealms.module.boss.backend.fabric.manager.BossSpawner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gg.mmorealms.module.boss.backend.fabric.BossMixinState;

/**
 * Wild battles are started by the R-key challenge (BattleChallengePacket → ChallengeHandler), NOT mobInteract.
 * This gate intercepts that path: challenging a boss opens the tier dialogue instead of starting the battle.
 * The dialogue's Battle button re-dispatches the same packet with the player's UUID in {@link BossMixinState#CONFIRMED},
 * which lets the second pass run Cobblemon's real battle pipeline.
 */
@Mixin(value = ChallengeHandler.class, remap = false)
public abstract class BossChallengeHandlerMixin {

	@Inject(
			method = "handle(Lcom/cobblemon/mod/common/net/messages/server/BattleChallengePacket;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerPlayer;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void mmoRealmsBoss$gateBossBattle(BattleChallengePacket packet, MinecraftServer server, ServerPlayer player, CallbackInfo ci) {
		if (BossMixinState.CONFIRMED.remove(player.getUUID())) {
			return;
		}
		Entity targeted = player.level().getEntity(packet.getTargetedEntityId());
		if (!(targeted instanceof PokemonEntity pokemonEntity)) {
			return;
		}
		if (!pokemonEntity.getPokemon().getPersistentData().getBoolean(NbtKeys.BOSS)) {
			return;
		}
		BossFabricModule mod = BossFabricModule.instance();
		BossSpawner spawner = mod != null ? mod.getBossSpawner() : null;
		if (spawner == null) {
			com.raduvoinea.utils.logger.Logger.warn("Boss challenge gate fired but BossSpawner is null — letting battle proceed.");
			return;
		}
		com.raduvoinea.utils.logger.Logger.info("Boss challenge gate intercepted " + player.getGameProfile().getName()
				+ "'s challenge against a boss-tagged Pokemon.");
		if (spawner.handleBossDialogue(player, pokemonEntity, packet)) {
			ci.cancel();
		}
	}
}
