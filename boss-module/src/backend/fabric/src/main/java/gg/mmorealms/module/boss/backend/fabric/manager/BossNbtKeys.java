package gg.mmorealms.module.boss.backend.fabric.manager;

/**
 * NBT key constants for boss data stored on Cobblemon Pokemon.persistentData. Schema v2.
 * <p>
 * NOTE: after mutating persistentData, callers MUST invoke
 * {@code pokemon.getAnyChangeObservable().emit(pokemon)} to mark the Pokemon dirty for
 * codec save — otherwise the change may revert (per Cobblemon Pokemon.kt:530-535). The
 * published API jar strips that accessor, so we rely on first-save-of-fresh-entity instead.
 */
public final class BossNbtKeys {

	private BossNbtKeys() {}

	public static final int SCHEMA_VERSION = 2;

	public static final String BOSS = "mmo_realms_boss";
	public static final String SCHEMA = "mmo_realms_boss_schema_version";
	public static final String TIER = "mmo_realms_boss_tier";
	public static final String SPECIES = "mmo_realms_boss_species";
	public static final String LEVEL = "mmo_realms_boss_level";
	public static final String SPAWNED_AT = "mmo_realms_boss_spawned_at";
	/** v2+ — true for proxy-scheduled spawns, false for admin spawns. Missing = true (legacy bosses count toward floor). */
	public static final String SYSTEM_SPAWNED = "mmo_realms_boss_system_spawned";
	/** v2+ — last-known spawn position for list fallback when entity unloaded. */
	public static final String SPAWN_X = "mmo_realms_boss_spawn_x";
	public static final String SPAWN_Y = "mmo_realms_boss_spawn_y";
	public static final String SPAWN_Z = "mmo_realms_boss_spawn_z";
	public static final String SPAWN_DIMENSION = "mmo_realms_boss_spawn_dim";
}
