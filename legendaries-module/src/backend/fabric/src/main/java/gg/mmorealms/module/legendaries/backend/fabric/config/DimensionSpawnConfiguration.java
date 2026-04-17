package gg.mmorealms.module.legendaries.backend.fabric.config;

public record DimensionSpawnConfiguration(
		int maxSearchHeight,
		int minSearchHeight,
		boolean checkSkyAccess
) {
}
