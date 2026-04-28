package gg.mmorealms.module.chat_games.backend.fabric.manager;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.chat_games.backend.common.manager.AbstractPokemonQuestionGenerator;
import gg.mmorealms.module.chat_games.common.dto.GeneratedQuestion;
import gg.mmorealms.module.chat_games.common.dto.QuestionType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CobblemonQuestionGenerator extends AbstractPokemonQuestionGenerator {

	private final EnumSet<QuestionType> loadedTypes = EnumSet.noneOf(QuestionType.class);

	public CobblemonQuestionGenerator() {
	}

	@Nullable
	private static String resolveTranslation(String key) {
		String resolved = Component.translatable(key).getString();
		if (resolved.equals(key) || resolved.isEmpty()) {
			return null;
		}
		return resolved;
	}

	private static String capitalize(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		return Character.toUpperCase(input.charAt(0)) + input.substring(1);
	}

	@Override
	protected boolean isAvailable(QuestionType type) {
		return switch (type) {
			case UNSCRAMBLE_POKEMON, DEX_ENTRY, POKEMON_TYPE, TYPE_POKEMON, POKEMON_ABILITY,
			     ABILITY_POKEMON, POKEMON_FORM, EGG_GROUP_POKEMON -> !PokemonSpecies.getImplemented().isEmpty();
			case UNSCRAMBLE_ABILITY -> !Abilities.all().isEmpty();
			case UNSCRAMBLE_MOVE -> !Moves.all().isEmpty();
			case UNSCRAMBLE_NATURE -> !Natures.all().isEmpty();
			case CUSTOM, MATH -> false; // handled on proxy
		};
	}

	@Override
	@Nullable
	public GeneratedQuestion generate(QuestionType type) {
		try {
			loadDataForType(type);
		} catch (Exception e) {
			Logger.error("[ChatGames] Failed to load Cobblemon data for " + type.name() + ": " + e);
			return null;
		}
		return super.generate(type);
	}

	private void loadDataForType(QuestionType type) {
		if (loadedTypes.contains(type)) {
			return;
		}

		switch (type) {
			case UNSCRAMBLE_POKEMON -> this.speciesNames = PokemonSpecies.getImplemented().stream()
				.map(Species::getName)
				.collect(Collectors.toList());
			case UNSCRAMBLE_ABILITY -> this.abilityNames = Abilities.all().stream()
				.map(template -> resolveTranslation(template.getDisplayName()))
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
			case UNSCRAMBLE_MOVE -> this.moveNames = Moves.all().stream()
				.map(move -> move.getDisplayName().getString())
				.filter(name -> !name.contains("."))
				.distinct()
				.collect(Collectors.toList());
			case UNSCRAMBLE_NATURE -> this.natureNames = Natures.all().stream()
				.map(nature -> capitalize(nature.getName().getPath()))
				.collect(Collectors.toList());
			case DEX_ENTRY -> this.dexEntries = resolveDexEntries(PokemonSpecies.getImplemented());
			case POKEMON_TYPE, TYPE_POKEMON -> this.pokemonTypes = resolvePokemonTypes(PokemonSpecies.getImplemented());
			case POKEMON_ABILITY -> this.pokemonAbilities = resolvePokemonAbilities(PokemonSpecies.getImplemented());
			case ABILITY_POKEMON -> this.abilityPokemon = resolveAbilityPokemon(PokemonSpecies.getImplemented());
			case POKEMON_FORM -> this.pokemonForms = resolvePokemonForms(PokemonSpecies.getImplemented());
			case EGG_GROUP_POKEMON -> this.eggGroupPokemon = resolveEggGroupPokemon(PokemonSpecies.getImplemented());
		}

		loadedTypes.add(type);
	}

	private Map<String, List<String>> resolvePokemonTypes(Collection<Species> implemented) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : implemented) {
			FormData form = species.getStandardForm();
			List<String> types = new ArrayList<>();

			for (ElementalType type : form.getTypes()) {
				types.add(capitalize(type.getName()));
			}

			if (!types.isEmpty()) {
				result.put(species.getName(), types);
			}
		}

		return result;
	}

	private Map<String, List<String>> resolvePokemonAbilities(Collection<Species> implemented) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : implemented) {
			Map<?, List<PotentialAbility>> mapping = species.getStandardForm().getAbilities().getMapping();

			List<String> abilities = mapping.values().stream()
				.flatMap(List::stream)
				.map(pa -> resolveTranslation(pa.getTemplate().getDisplayName()))
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());

			if (!abilities.isEmpty()) {
				result.put(species.getName(), abilities);
			}
		}

		return result;
	}

	private Map<String, String> resolveDexEntries(Collection<Species> implemented) {
		Map<String, String> result = new HashMap<>();

		for (Species species : implemented) {
			List<String> pokedex = species.getPokedex();

			if (!pokedex.isEmpty()) {
				String key = pokedex.getFirst();
				String resolved = Component.translatable(key).getString();

				if (!resolved.equals(key) && !resolved.isEmpty()) {
					String censored = Pattern.compile(Pattern.quote(species.getName()), Pattern.CASE_INSENSITIVE)
						.matcher(resolved)
						.replaceAll("this Pokémon");
					result.put(species.getName(), censored);
				}
			}
		}

		return result;
	}

	private Map<String, List<String>> resolveAbilityPokemon(Collection<Species> implemented) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : implemented) {
			Map<String, Set<String>> abilityToAnswers = new LinkedHashMap<>();

			List<FormData> forms = species.getForms().isEmpty()
				? List.of(species.getStandardForm())
				: species.getForms();
			for (FormData form : forms) {
				List<String> formAbilities = form.getAbilities().getMapping().values().stream()
					.flatMap(List::stream)
					.map(pa -> resolveTranslation(pa.getTemplate().getDisplayName()))
					.filter(Objects::nonNull)
					.distinct()
					.toList();
				String megaName = form.getAspects().stream()
					.map(aspect -> megaAnswerName(species.getName(), aspect))
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(null);

				for (String ability : formAbilities) {
					Set<String> answers = abilityToAnswers.computeIfAbsent(ability, k -> new LinkedHashSet<>());
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

	private Map<String, List<String>> resolvePokemonForms(Collection<Species> implemented) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : implemented) {
			if (EXCLUDED_FORM_POKEMON.contains(species.getName())) {
				continue;
			}

			for (FormData form : species.getForms()) {
				String formName = form.getName();

				if (formName.equalsIgnoreCase("Normal") || formName.contains("-") || EXCLUDED_FORM_NAMES.contains(formName.toLowerCase())) {
					continue;
				}

				result.computeIfAbsent(formName, k -> new ArrayList<>()).add(species.getName());
			}
		}

		result.entrySet().removeIf(entry -> entry.getValue().size() < 2);

		return result;
	}

	private Map<String, List<String>> resolveEggGroupPokemon(Collection<Species> implemented) {
		Map<String, List<String>> result = new HashMap<>();

		for (Species species : implemented) {
			for (EggGroup eggGroup : species.getEggGroups()) {
				String displayName = eggGroupDisplayName(eggGroup.getShowdownID());
				if (displayName == null) {
					continue;
				}
				result.computeIfAbsent(displayName, k -> new ArrayList<>()).add(species.getName());
			}
		}

		return result;
	}

}
