package gg.mmorealms.module.legendaries.backend.fabric.config;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.module.legendaries.backend.fabric.dto.LegendarySpawnData;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.level.Level;

import java.util.*;

public class LegendarySpawnConfig {
	public List<String> allowedDimensions = List.of(
			Level.OVERWORLD.location().toString(),
			Level.NETHER.location().toString(),
			Level.END.location().toString()
	);
	public Map<String, DimensionSpawnConfiguration> dimensionConfigurations = new HashMap<>() {{
		put(Level.OVERWORLD.location().toString(), new DimensionSpawnConfiguration(
				320,
				-64,
				true
		));

		put(Level.NETHER.location().toString(), new DimensionSpawnConfiguration(
				120, // Below bedrock ceiling
				-64,
				false
		));

		put(Level.END.location().toString(), new DimensionSpawnConfiguration(
				320,
				-64,
				true
		));
	}};

	public int maxCandidateAttempts = 100;
	public Time positionUpdateInterval = Time.seconds(30);

	// Whether to ignore leaves as valid spawn position for legendaries
	// In future can be moved to config as parameter per legendary
	public boolean ignoreLeaves = true;

	public boolean isCaptureForChosenPlayerOnly = true;

	// Whether to spawn legendary when no players are on wild
	public boolean spawnOnPlayersAbsent = true;

	// Defines range at which legendary can be spawned around player.
	// Anything lower than min, will be treated as not valid positions.
	// Effectively creating square 'donut' shaped spawn zone
	public Range spawnRange = new Range(50, 500);

	// Fallback values when no players are on wild:

	// Defines random range in which position would be chosen
	// Does not constrain spawn to it.
	// Biome, and spawn point for it, could be found outside of this range
	public Range randomSpawnRangeX = new Range(-10000, 10000);
	public Range randomSpawnRangeY = new Range(-60, 320);
	public Range randomSpawnRangeZ = new Range(-10000, 10000);

	// Ideally should be between 'underground' biomes (e.g., deep dark, lush caves),
	// and surface biomes (e.g, plains, taiga, etc)
	public int biomeSearchStartPositionY = 50;
	public int biomeSearchRadius = 2000;
	public int biomeSearchHorizontalStep = 16;
	public int biomeSearchVerticalStep = 8;
	public int canPokemonFitCheckRadius = 2;

	public int legendaryChunkLoadingRadius = 1;
	public Time legendaryChunkLoadingExpiry = Time.seconds(10);

	// List of Pokémon aspects used in config
	// E.g. galarian, hisuian, paldean, alolan
	public List<String> aspects = Arrays.asList("galarian");

	/* ---------- Data ---------- */

	public LegendarySpawnData legendaries = new LegendarySpawnData();

	public Lang lang = new Lang();

	public static class Lang {
		public MessageBuilder onlyAllowedCatcher = new MessageBuilder("<red>Only player, for which pokemon has spawned, and members (with trust level {trustLevel}) of their realm can capture this legendary");
		public MessageBuilder failedLegendarySpawn = new MessageBuilder("<red>Failed to spawn Legendary");
	}

	public LegendarySpawnConfig() {
		validateAndLogAllSpecies();
	}

	/* ========== PRIVATE ========== */

	/* ---------- Debug ---------- */

	/**
	 * Validates all legendary Pokémon names using production logic.
	 * If any species is invalid, the method will print debug information and throw a runtime exception.
	 */
	private void validateAndLogAllSpecies() {
		Map<String, NameValidationResult> results = validateLegendaries();
		int totalCount = results.size();
		int validCount = (int) results.values().stream().filter(NameValidationResult::isValid).count();

		if (CommonLoader.DEBUG_MODE) {
			Logger.debug("\n=== Pokemon Name Validation Results [" + validCount + "/" + totalCount + "] ===");

			Logger.debug("\nValid Pokemon Names:");
			results.entrySet().stream()
					.filter(e -> e.getValue().isValid())
					.sorted(Map.Entry.comparingByKey())
					.forEach(e -> {
						NameValidationResult result = e.getValue();
						String aspectInfo = result.getAppliedAspects().isEmpty() ? "" :
								" (aspects: " + result.getAppliedAspects() + ")";
						Logger.debug("✓ " + e.getKey() + ": " + result.getProcessedName() + aspectInfo);
					});
		}

		Logger.debug("\nInvalid Pokemon Names:");
		results.entrySet().stream()
				.filter(e -> !e.getValue().isValid())
				.sorted(Map.Entry.comparingByKey())
				.forEach(e -> {
					NameValidationResult result = e.getValue();
					Logger.error("✗ " + e.getKey() + ": " + result.getOriginalName() +
							" - " + result.getErrorMessage());
				});

		if (validCount != totalCount) {
			StringBuilder errorMessage = new StringBuilder("Some Pokémon species are invalid:\n");
			results.entrySet().stream()
					.filter(e -> !e.getValue().isValid())
					.forEach(e -> errorMessage.append(e.getKey())
							.append(e.getValue().getErrorMessage())
							.append("\n"));
			throw new RuntimeException(errorMessage.toString());
		}
	}

	private Map<String, NameValidationResult> validateLegendaries() {
		Map<String, NameValidationResult> results = new HashMap<>();
		List<String> currentAspects = aspects;

		for (Map.Entry<String, LegendarySpawnData.PokemonData> entry : legendaries.entrySet()) {
			NameValidationResult result = validatePokemonName(entry.getValue().getSpec(), currentAspects);
			results.put(entry.getKey(), result);
		}
		return results;
	}

	private NameValidationResult validatePokemonName(String name, List<String> aspects) {
		NameValidationResult result = new NameValidationResult(name);
		String processedName = name;

		// Remove known aspects from the name.
		for (String aspect : aspects) {
			if (processedName.contains(aspect)) {
				processedName = processedName.replace(aspect, "").trim();
				result.addAppliedAspect(aspect);
			}
		}
		result.setProcessedName(processedName);

		if (processedName.isEmpty()) {
			result.setValid(false);
			result.setErrorMessage("Empty name after aspect removal");
		} else if (!processedName.matches("[A-Za-z0-9\\-\\s]+")) {
			result.setValid(false);
			result.setErrorMessage("Name contains invalid characters");
		} else {
			if (validatePokemonSpecies(processedName)) {
				result.setValid(true);
			} else {
				result.setValid(false);
				result.setErrorMessage("Can't get PokemonSpecies by name: " + processedName);
			}
		}
		return result;
	}

	private boolean validatePokemonSpecies(String name) {
		try {
			Species species = PokemonSpecies.INSTANCE.getByName(name);
			return species != null;
		} catch (RuntimeException e) {
			return false;
		}
	}

	@Getter
	@Setter
	private static class NameValidationResult {
		private final String originalName;
		private String processedName;
		private boolean valid;
		private String errorMessage = "";
		private List<String> appliedAspects = new ArrayList<>();

		public NameValidationResult(String originalName) {
			this.originalName = originalName;
		}

		public void addAppliedAspect(String aspect) {
			appliedAspects.add(aspect);
		}
	}
}
