package gg.mmorealms.module.boss.backend.fabric.manager;

import gg.mmorealms.module.boss.common.BossTier;

import java.util.UUID;

public record ActiveBoss(
		UUID pokemonUUID,
		UUID entityUUID,
		BossTier tier,
		String species,
		int level,
		long spawnedAtEpoch
) {}
