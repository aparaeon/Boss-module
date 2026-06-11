package gg.mmorealms.module.boss.backend.fabric.manager;

/** NBT keys for boss data on {@code Pokemon.persistentData}. Schema v2. */
public final class BossNbtKeys {

	private BossNbtKeys() {}

	public static final int SCHEMA_VERSION = 2;

	public static final String BOSS = "mmo_realms_boss";
	public static final String SCHEMA = "mmo_realms_boss_schema_version";
	public static final String TIER = "mmo_realms_boss_tier";
	public static final String SPECIES = "mmo_realms_boss_species";
	public static final String LEVEL = "mmo_realms_boss_level";
	public static final String SPAWNED_AT = "mmo_realms_boss_spawned_at";
	/** True for system spawns, false for admin. Missing = true (legacy fallback). */
	public static final String SYSTEM_SPAWNED = "mmo_realms_boss_system_spawned";
	/** Last-known spawn position — used as fallback when entity is unloaded. */
	public static final String SPAWN_X = "mmo_realms_boss_spawn_x";
	public static final String SPAWN_Y = "mmo_realms_boss_spawn_y";
	public static final String SPAWN_Z = "mmo_realms_boss_spawn_z";
	public static final String SPAWN_DIMENSION = "mmo_realms_boss_spawn_dim";
}
