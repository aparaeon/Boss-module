package gg.mmorealms.module.legendaries.backend.fabric.mixin;

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.legendaries.backend.fabric.LegendariesModule;
import gg.mmorealms.module.legendaries.backend.fabric.config.LegendarySpawnConfig;
import gg.mmorealms.module.legendaries.backend.fabric.dto.ICaptureLockable;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

/**
 * Mixin to deny Pokemon catch if catcher is not 'selected player' of spawned legendary
 * We need to do this, instead of subscribing and canceling event.
 * As cancelling THROWN_POKEBALL_HIT while in battle will freeze opponent Pokemon
 */
@Mixin(EmptyPokeBallEntity.class)
public abstract class EmptyPokeBallEntityMixin extends ThrowableItemProjectile {

	public EmptyPokeBallEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	protected abstract void drop();

	@Inject(
			method = "onHitEntity",
			at = @At("HEAD"),
			cancellable = true)
	private void onHitEntity(EntityHitResult hitResult, CallbackInfo ci) {
		// Early exit for non-pokemon entities
		if (!(hitResult.getEntity() instanceof PokemonEntity pokemonEntity)) {
			return;
		}

		// Normal method flow if any player can catch legendary
		if (!LegendariesModule.instance().getConfig().isCaptureForChosenPlayerOnly) {
			return;
		}

		// Just safe cast, should never return
		if (!(getOwner() instanceof ServerPlayer catcher)) {
			return;
		}

		// Normal method flow if allowedCatchers are not setup
		Set<UUID> allowedCatchers = ((ICaptureLockable) pokemonEntity).core$getAllowedCatchers();
		if (allowedCatchers == null) {
			return;
		}

		// Deny catch and drop pokeball, if catcher is not allowed
		if (!allowedCatchers.contains(catcher.getUUID())) {

			LegendarySpawnConfig config = LegendariesModule.instance().getConfig();

			MessageBuilder message = config.lang.onlyAllowedCatcher
					.parse("trustLevel",
							RealmPermission.LEGENDARY_CAPTURE_SHARED.getLevel().getName());

			User.get(catcher).sendMessage(message);

			drop();
			ci.cancel();
		}
	}
}