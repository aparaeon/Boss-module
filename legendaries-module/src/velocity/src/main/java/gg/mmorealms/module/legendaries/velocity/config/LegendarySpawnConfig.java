package gg.mmorealms.module.legendaries.velocity.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;

import java.util.Arrays;

public class LegendarySpawnConfig {

	public float baseSpawnChance = 0.1f; // 0%-100%
	public float playerSpawnChanceBias = 0.01f;
	public float playerSpawnMultiplierBias = 0.001f;
	public Time spawnAttemptCooldown = Time.seconds(30);
	public Time despawnTime = Time.minutes(30);
	public boolean sendSpawnRequestToEmptyServer = true;

	// ---------- Broadcast ----------

	public boolean printPokemonName = true;
	public boolean printPlayerName = true;
	public boolean printSpawnDimension = true;
	public boolean printSpawnBiome = true;
	public boolean printSpawnLocation = true;
	public boolean obfuscateSpawnLocation = true;
	// Defines range of possible radius offset, should be positive
	// In offset annotation takes max of range
	public Range obfuscationRange = new Range(500, 1000);

	public Lang lang = new Lang();

	public static class Lang {
		public String defaultName = "Legendary pokemon";
		public MessageBuilder playerAnnotation = new MessageBuilder("by {player}");
		public MessageBuilder dimensionAnnotation = new MessageBuilder("Somewhere in {dimension}");
		public MessageBuilder biomeAnnotation = new MessageBuilder("{biome}");
		public MessageBuilder locationAnnotation = new MessageBuilder("around <red>{x}, <green>{y}, <blue>{z}");
		public MessageBuilder offsetAnnotation = new MessageBuilder("<hover:show_text:'Range at which real XZ coordinates are located'><white>[±{offset}]</hover>");

		public MessageBuilderList legendarySpawned = new MessageBuilderList(Arrays.asList(
				"<yellow>=====================================================",
				"<gradient:#d4af37:yellow:#d4af37><b>{name}</b></gradient> <yellow>has appeared!",
				"<yellow>{dimensionAnnotation}, {biomeAnnotation} {locationAnnotation} {offsetAnnotation}",
				"<yellow>====================================================="
		));
		public MessageBuilderList legendaryDespawned = new MessageBuilderList(Arrays.asList(
				"<red>=====================================================",
				"<gradient:#d4af37:yellow:#d4af37><b>{name}</b></gradient> <red>got despawned!",
				"<red>====================================================="
		));
		public MessageBuilderList legendaryCaptured = new MessageBuilderList(Arrays.asList(
				"<green>=====================================================",
				"<gradient:#d4af37:yellow:#d4af37><b>{name}</b></gradient> <green>got captured {playerAnnotation}!",
				"<green>====================================================="
		));
		public MessageBuilderList legendaryKilled = new MessageBuilderList(Arrays.asList(
				"<red>=====================================================",
				"<gradient:#d4af37:yellow:#d4af37><b>{name}</b></gradient> <red>got killed!",
				"<red>====================================================="
		));

		public MessageBuilder wrongUUID = new MessageBuilder("<red>Wrong UUID");
		public MessageBuilder spawningLegendary = new MessageBuilder("<gray>Spawning legendary...");
		public MessageBuilder despawningLegendary = new MessageBuilder("<gray>Despawning legendary...");
		public MessageBuilder noPlayersOnWilds = new MessageBuilder("<red>None of wild servers have players. Please use argument 'true' to be able to spawn on an empty servers");
	}
}
