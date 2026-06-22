package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.manager.BossSpawner;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.NbtKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(PokemonEntity.class)
public abstract class BossPokemonDialogueMixin {

	/** UUIDs of players whose next interact should skip dialogue and go straight to battle. */
	public static final Set<UUID> BATTLE_TRIGGERED = new HashSet<>();

	@Inject(method = "mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
			at = @At("HEAD"), cancellable = true)
	private void mmoRealmsBoss$showDialogue(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		if (BATTLE_TRIGGERED.remove(serverPlayer.getUUID())) {
			return;
		}
		mmoRealmsBoss$maybeShowDialogue(serverPlayer, hand, cir);
	}

	private void mmoRealmsBoss$maybeShowDialogue(ServerPlayer player, InteractionHand hand,
	                                              CallbackInfoReturnable<InteractionResult> cir) {
		PokemonEntity self = (PokemonEntity) (Object) this;
		CompoundTag tag = self.getPokemon().getPersistentData();
		if (!tag.getBoolean(NbtKeys.BOSS)) {
			return;
		}
		long now = self.level().getGameTime();
		if (tag.getLong("mmo_realms_boss_last_dialogue_tick") == now) {
			return;
		}
		tag.putLong("mmo_realms_boss_last_dialogue_tick", now);

		BossFabricModule mod = BossFabricModule.instance();
		BossSpawner spawner = mod != null ? mod.getBossSpawner() : null;
		if (spawner == null) {
			return;
		}
		spawner.handleBossDialogue(player, self, hand);
		cir.setReturnValue(InteractionResult.SUCCESS);
	}
}
