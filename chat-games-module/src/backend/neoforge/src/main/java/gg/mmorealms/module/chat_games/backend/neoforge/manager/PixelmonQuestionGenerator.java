package gg.mmorealms.module.chat_games.backend.neoforge.manager;

import com.pixelmonmod.api.registry.RegistryManager;
import com.pixelmonmod.pixelmon.api.pokemon.Nature;
import com.pixelmonmod.pixelmon.api.pokemon.ability.Ability;
import com.pixelmonmod.pixelmon.api.pokemon.egg.EggGroup;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.abilities.Abilities;
import com.pixelmonmod.pixelmon.api.pokemon.species.moves.Moves;
import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.battles.attacks.ImmutableAttack;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.chat_games.backend.common.manager.AbstractPokemonQuestionGenerator;
import gg.mmorealms.module.chat_games.common.dto.GeneratedQuestion;
import gg.mmorealms.module.chat_games.common.dto.QuestionType;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PixelmonQuestionGenerator extends AbstractPokemonQuestionGenerator {

	private final EnumSet<QuestionType> loadedTypes = EnumSet.noneOf(QuestionType.class);

	private static String capitalize(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		return Character.toUpperCase(input.charAt(0)) + input.substring(1);
	}

	private List<Species> getAllSpecies() {
		return RegistryManager.getAllValues(Species.class);
	}

	@Override
	protected boolean isAvailable(QuestionType type) {
		return switch (type) {
			case UNSCRAMBLE_POKEMON, POKEMON_TYPE, TYPE_POKEMON, POKEMON_ABILITY,
			     ABILITY_POKEMON, POKEMON_FORM, EGG_GROUP_POKEMON,
			     UNSCRAMBLE_ABILITY, UNSCRAMBLE_MOVE, DEX_ENTRY -> !getAllSpecies().isEmpty();
			case UNSCRAMBLE_NATURE -> Nature.values().length > 0;
			case CUSTOM, MATH -> false;
		};
	}

	@Override
	@Nullable
	public GeneratedQuestion generate(QuestionType type) {
		try {
			loadDataForType(type);
		} catch (Exception e) {
			Logger.error("[ChatGames] Failed to load Pixelmon data for " + type.name() + ": " + e);
			return null;
		}
		return super.generate(type);
	}

	private void loadDataForType(QuestionType type) {
		if (loadedTypes.contains(type)) {
			return;
		}

		List<Species> allSpecies = getAllSpecies();

		switch (type) {
			case UNSCRAMBLE_POKEMON -> this.speciesNames = allSpecies.stream()
				.map(Species::getName)
				.collect(Collectors.toList());

			case UNSCRAMBLE_ABILITY -> this.abilityNames = resolveAllAbilityNames(allSpecies);

			case UNSCRAMBLE_MOVE -> this.moveNames = resolveAllMoveNames(allSpecies);

			case UNSCRAMBLE_NATURE -> this.natureNames = Arrays.stream(Nature.values())
				.map(n -> capitalize(n.getTranslatedName().getString()))
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());

			case DEX_ENTRY -> this.dexEntries = resolveDexEntries(allSpecies);

			case POKEMON_TYPE, TYPE_POKEMON -> this.pokemonTypes = resolvePokemonTypes(allSpecies);

			case POKEMON_ABILITY -> this.pokemonAbilities = resolvePokemonAbilities(allSpecies);

			case ABILITY_POKEMON -> this.abilityPokemon = resolveAbilityPokemon(allSpecies);

			case POKEMON_FORM -> this.pokemonForms = resolvePokemonForms(allSpecies);

			case EGG_GROUP_POKEMON -> this.eggGroupPokemon = resolveEggGroupPokemon(allSpecies);
		}

		Logger.info("[ChatGames] Loaded Pixelmon data for " + type.name());
		loadedTypes.add(type);
	}

	private Map<String, String> resolveDexEntries(List<Species> allSpecies) {
		Map<String, String> result = new HashMap<>();

		for (Species species : allSpecies) {
			try {
				String key = species.getDescTranslationKey();
				String translated = species.getDescTranslation().getString();

				if (translated.equals(key) || translated.length() < 20) {
					continue;
				}

				// (?i) makes the match case-insensitive so "Pikachu", "PIKACHU", and "pikachu" all get replaced
				String censored = translated.replaceAll(
					"(?i)" + Pattern.quote(species.getName()),
					"this Pokémon"
				);

				result.put(species.getName(), censored);
			} catch (Exception ignored) {
			}
		}

		Logger.info("[ChatGames] Dex entries loaded: " + result.size());
		return result;
	}


	private List<String> resolveAllAbilityNames(List<Species> allSpecies) {
		Set<String> names = new LinkedHashSet<>();

		for (Species species : allSpecies) {
			for (Stats form : species.getForms()) {
				if (form == null) {
					continue;
				}

				Abilities abilities = form.getAbilities();
				if (abilities == null) {
					continue;
				}

				for (Ability ability : abilities.getAll()) {
					if (ability == null) {
						continue;
					}

					String name = capitalize(ability.getName());
					if (name != null && !name.isEmpty() && !name.contains(".")) {
						names.add(name);
					}
				}
			}
		}

		return new ArrayList<>(names);
	}

	private List<String> resolveAllMoveNames(List<Species> allSpecies) {
		Set<String> names = new LinkedHashSet<>();

		for (Species species : allSpecies) {
			for (Stats form : species.getForms()) {
				if (form == null) {
					continue;
				}

				Moves moves = form.getMoves();
				if (moves == null) {
					continue;
				}

				for (ImmutableAttack attack : moves.getAllMoves()) {
					if (attack == null) {
						continue;
					}

					String name = capitalize(attack.getAttackName());
					if (name != null && !name.isEmpty() && !name.contains(".")) {
						names.add(name);
					}
				}
			}
		}

		return new ArrayList<>(names);
	}

	private Map<String, List<String>> resolvePokemonTypes(List<Species> allSpecies) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : allSpecies) {
			Stats base = species.getFirstForm();
			List<String> types = new ArrayList<>();

			for (Holder<Type> typeHolder : base.getTypes()) {
				types.add(capitalize(typeHolder.value().name().getString()));
			}

			if (!types.isEmpty()) {
				result.put(species.getName(), types);
			}
		}

		return result;
	}

	private Map<String, List<String>> resolvePokemonAbilities(List<Species> allSpecies) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : allSpecies) {
			Stats base = species.getFirstForm();
			Abilities abilities = base.getAbilities();
			if (abilities == null) {
				continue;
			}

			List<String> abilitiesList = new ArrayList<>();
			for (Ability ability : abilities.getAll()) {
				if (ability == null) {
					continue;
				}
				String name = capitalize(ability.getName());
				if (name != null && !name.isEmpty()) {
					abilitiesList.add(name);
				}
			}

			if (!abilitiesList.isEmpty()) {
				result.put(species.getName(), abilitiesList);
			}
		}

		return result;
	}

	private Map<String, List<String>> resolveAbilityPokemon(List<Species> allSpecies) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : allSpecies) {
			Map<String, Set<String>> abilityToAnswers = new LinkedHashMap<>();

			for (Stats form : species.getForms()) {
				if (form == null) {
					continue;
				}
				Abilities abilities = form.getAbilities();
				if (abilities == null) {
					continue;
				}

				String megaName = megaAnswerName(species.getName(), form.getName());

				for (Ability ability : abilities.getAll()) {
					if (ability == null) {
						continue;
					}
					String name = capitalize(ability.getName());
					if (name == null || name.isEmpty()) {
						continue;
					}
					Set<String> answers = abilityToAnswers.computeIfAbsent(name, k -> new LinkedHashSet<>());
					answers.add(species.getName());
					if (megaName != null) {
						answers.add(megaName);
					}
				}
			}

			for (Map.Entry<String, Set<String>> entry : abilityToAnswers.entrySet()) {
				result.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue());
			}
		}

		return result;
	}

	private Map<String, List<String>> resolvePokemonForms(List<Species> allSpecies) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : allSpecies) {
			if (EXCLUDED_FORM_POKEMON.contains(species.getName())) {
				continue;
			}

			for (Stats form : species.getForms()) {
				if (form == null) {
					continue;
				}

				String formName = form.getName();
				if (formName == null ||
					formName.equalsIgnoreCase("Normal") ||
					formName.equalsIgnoreCase("Standard") ||
					formName.equalsIgnoreCase("Base") ||
					formName.contains("-")) {
					continue;
				}

				result.computeIfAbsent(formName, k -> new ArrayList<>())
					.add(species.getName());
			}
		}

		result.entrySet().removeIf(e -> e.getValue().size() < 2);
		return result;
	}


	private Map<String, List<String>> resolveEggGroupPokemon(List<Species> allSpecies) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : allSpecies) {
			Stats base = species.getFirstForm();

			for (EggGroup eggGroup : base.getEggGroups()) {
				if (eggGroup == null) {
					continue;
				}

				String displayName = eggGroupDisplayName(eggGroup.getKey());
				if (displayName == null) {
					continue;
				}

				result.computeIfAbsent(displayName, k -> new ArrayList<>())
					.add(species.getName());
			}
		}

		return result;
	}
}