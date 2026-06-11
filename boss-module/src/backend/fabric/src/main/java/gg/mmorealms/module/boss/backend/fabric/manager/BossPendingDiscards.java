package gg.mmorealms.module.boss.backend.fabric.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Persisted tombstones for bosses despawned while their chunk was unloaded.
 * Consumed on the next ENTITY_LOAD so admin despawns survive server restarts.
 */
public class BossPendingDiscards {

	public Set<UUID> entityUUIDs = new HashSet<>();
}
