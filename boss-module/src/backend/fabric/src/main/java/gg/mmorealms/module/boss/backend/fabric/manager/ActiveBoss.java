package gg.mmorealms.module.boss.backend.fabric.manager;

import gg.mmorealms.module.boss.common.BossTier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record ActiveBoss(
		UUID pokemonUUID,
		UUID entityUUID,
		BossTier tier,
		String species,
		int level,
		long spawnedAtEpoch,
		/** True for proxy-scheduled spawns, false for admin commands. Drives tier-counter accounting. */
		boolean systemSpawned,
		/** Last-known spawn position — used as list fallback when the entity isn't currently loaded. */
		BlockPos spawnPos,
		/** Dimension id at spawn time (e.g. {@code minecraft:overworld}) — list fallback when unloaded. */
		ResourceLocation spawnDimension
) {}
