package gg.mmorealms.module.boss.backend.fabric.manager;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * NBT key constants for boss data stored on Cobblemon Pokemon.persistentData.
 * Schema version 1.
 * <p>
 * NOTE: after mutating persistentData, callers MUST invoke
 * {@code pokemon.getAnyChangeObservable().emit(pokemon)}
 * to mark the Pokemon dirty for codec save — otherwise the change may revert
 * (per Cobblemon Pokemon.kt:530-535 docstring). The published API jar currently
 * strips that accessor, so we rely on first-save-of-fresh-entity instead — see
 * the BossSpawner spawn pipeline comments for details.
 */
public final class BossNbtKeys {

	private BossNbtKeys() {}

	public static final int SCHEMA_VERSION = 1;

	public static final String BOSS = "mmo_realms_boss";
	public static final String SCHEMA = "mmo_realms_boss_schema_version";
	public static final String TIER = "mmo_realms_boss_tier";
	public static final String SPECIES = "mmo_realms_boss_species";
	public static final String LEVEL = "mmo_realms_boss_level";
	public static final String SPAWNED_AT = "mmo_realms_boss_spawned_at";

	/**
	 * Returns {@code true} if the given Pokemon was spawned as a boss by this module.
	 * Reads only the boolean marker; does not validate schema version. Safe to call
	 * on any Pokemon (returns false for non-bosses or null persistentData).
	 */
	public static boolean isBoss(@Nullable Pokemon pokemon) {
		if (pokemon == null) return false;
		CompoundTag tag = pokemon.getPersistentData();
		return tag != null && tag.getBoolean(BOSS);
	}

	/** Convenience overload for entity-side callers. */
	public static boolean isBoss(@NotNull PokemonEntity entity) {
		return isBoss(entity.getPokemon());
	}
}
