package gg.mmorealms.module.legendaries.backend.fabric.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.legendaries.backend.fabric.dto.ICaptureLockable;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;
import java.util.UUID;

/**
 * Mixin to store allowedCatchers for Legendary capture locking.
 * While creating a separate class which extends PokemonEntity would be easier,
 * it seems impossible because of return type discrepancy between PokemonEntity and AgeableMob methods
 * caused by Kotlin with Java interaction
 */
@Accessors(fluent = true, chain = true)
@Mixin(PokemonEntity.class)
public class PokemonEntityMixin implements ICaptureLockable {

	@Unique
	@Nullable
	private Set<UUID> allowedCatchers = null;

	@Override
	public @Nullable Set<UUID> core$getAllowedCatchers() {
		return allowedCatchers;
	}

	@Override
	public void core$setAllowedCatchers(@Nullable Set<UUID> allowedCatchers) {
		this.allowedCatchers = allowedCatchers;
	}
}