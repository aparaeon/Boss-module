package gg.mmorealms.module.legendaries.common.dto;

import gg.mmorealms.loader.common.dto.location.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LegendaryInfo {

	// Common
	private String pokemonName;
	private UUID pokemonEntityUUID;
	private UUID pokemonUUID;
	private String serverID;

	// Spawn specific
	private String levelName;
	private String biomeName;
	private Location location;
	private long spawnTime;

	// Catch specific
	private String interactedPlayerName;
}
