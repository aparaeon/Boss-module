package gg.mmorealms.module.chat_games.backend.common.manager;

import com.raduvoinea.utils.generic.RandomUtils;
import gg.mmorealms.module.chat_games.common.dto.GeneratedQuestion;
import gg.mmorealms.module.chat_games.common.dto.QuestionType;
import gg.mmorealms.module.chat_games.common.utils.AnswerNormalizer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractPokemonQuestionGenerator implements PokemonQuestionGenerator {

	// Pokémon excluded entirely from POKEMON_FORM questions.
	protected static final Set<String> EXCLUDED_FORM_POKEMON = Set.of("Arceus", "Silvally", "Gourgeist", "Floette");

	// Form names that are too vague as questions — type names or generic size descriptors.
	// "Normal" is excluded separately in the loop; the rest are handled here.
	protected static final Set<String> EXCLUDED_FORM_NAMES = Set.of(
		"fire", "water", "electric", "grass", "ice", "fighting", "poison",
		"ground", "flying", "psychic", "bug", "rock", "ghost", "dragon",
		"dark", "steel", "fairy",
		"small", "average", "large"
	);

	// Derives a Mega display name from a form name, e.g. "mega" → "Mega Aggron", "mega_x" → "Mega Charizard X".
	// Returns null if the form is not a Mega form.
	@Nullable
	protected static String megaAnswerName(String speciesName, String formName) {
		if (formName == null) {
			return null;
		}
		String lower = formName.toLowerCase().replace(" ", "_");
		if (!lower.startsWith("mega")) {
			return null;
		}
		String suffix = lower.substring(4).replace("_", " ").trim().toUpperCase();
		return suffix.isEmpty() ? "Mega " + speciesName : "Mega " + speciesName + " " + suffix;
	}

	private static final Map<String, String> EGG_GROUP_DISPLAY_NAMES = Map.ofEntries(
		Map.entry("monster", "Monster"),
		Map.entry("water1", "Water 1"),
		Map.entry("water2", "Water 2"),
		Map.entry("water3", "Water 3"),
		Map.entry("bug", "Bug"),
		Map.entry("flying", "Flying"),
		Map.entry("field", "Field"),
		Map.entry("fairy", "Fairy"),
		Map.entry("grass", "Grass"),
		Map.entry("humanlike", "Human-Like"),
		Map.entry("mineral", "Mineral"),
		Map.entry("amorphous", "Amorphous"),
		Map.entry("ditto", "Ditto"),
		Map.entry("dragon", "Dragon"),
		Map.entry("undiscovered", "Undiscovered")
	);

	@Nullable
	protected static String eggGroupDisplayName(String key) {
		if (key == null) {
			return null;
		}
		return EGG_GROUP_DISPLAY_NAMES.get(key.toLowerCase());
	}

	protected List<String> speciesNames = List.of();
	protected List<String> abilityNames = List.of();
	protected List<String> moveNames = List.of();
	protected List<String> natureNames = List.of();
	protected Map<String, List<String>> pokemonTypes = Map.of();
	protected Map<String, List<String>> pokemonAbilities = Map.of();
	protected Map<String, List<String>> abilityPokemon = Map.of();
	protected Map<String, List<String>> pokemonForms = Map.of();
	protected Map<String, List<String>> eggGroupPokemon = Map.of();
	protected Map<String, String> dexEntries = Map.of();

	@Override
	public QuestionType pickWeightedRandom(@Nullable String lastKey) {
		List<QuestionType> candidates = Arrays.stream(QuestionType.values())
			.filter(type -> lastKey == null || !type.getKey().equals(lastKey))
			.filter(this::isAvailable)
			.toList();

		if (candidates.isEmpty()) {
			candidates = Arrays.stream(QuestionType.values())
				.filter(this::isAvailable)
				.toList();
		}

		return RandomUtils.getRandomWeighed(candidates);
	}

	protected boolean isAvailable(QuestionType type) {
		return switch (type) {
			case UNSCRAMBLE_POKEMON -> !speciesNames.isEmpty();
			case UNSCRAMBLE_ABILITY -> !abilityNames.isEmpty();
			case UNSCRAMBLE_MOVE -> !moveNames.isEmpty();
			case UNSCRAMBLE_NATURE -> !natureNames.isEmpty();
			case DEX_ENTRY -> !dexEntries.isEmpty();
			case POKEMON_TYPE,
			     TYPE_POKEMON -> !pokemonTypes.isEmpty();
			case POKEMON_ABILITY -> !pokemonAbilities.isEmpty();
			case ABILITY_POKEMON -> !abilityPokemon.isEmpty();
			case POKEMON_FORM -> !pokemonForms.isEmpty();
			case EGG_GROUP_POKEMON -> !eggGroupPokemon.isEmpty();
			case CUSTOM, MATH -> false; // handled on proxy
		};
	}

	@Override
	@Nullable
	public GeneratedQuestion generate(QuestionType type) {
		return switch (type) {
			case UNSCRAMBLE_POKEMON -> buildUnscramble(speciesNames, type);
			case UNSCRAMBLE_ABILITY -> buildUnscramble(abilityNames, type);
			case UNSCRAMBLE_MOVE -> buildUnscramble(moveNames, type);
			case UNSCRAMBLE_NATURE -> buildUnscramble(natureNames, type);
			case DEX_ENTRY -> buildDexEntry();
			case POKEMON_TYPE -> buildPokemonType();
			case TYPE_POKEMON -> buildTypePokemon();
			case POKEMON_ABILITY -> buildPokemonAbility();
			case ABILITY_POKEMON -> buildAbilityPokemon();
			case POKEMON_FORM -> buildPokemonForm();
			case EGG_GROUP_POKEMON -> buildEggGroupPokemon();
			case CUSTOM, MATH -> null; // handled on proxy
		};
	}

	@Nullable
	private GeneratedQuestion buildUnscramble(List<String> pool, QuestionType type) {
		if (pool.isEmpty()) {
			return null;
		}

		String word = RandomUtils.getRandom(pool);
		String normalizedWord = AnswerNormalizer.normalize(word);

		Set<String> answers = new HashSet<>();
		answers.add(normalizedWord);

		return new GeneratedQuestion(type.getKey(), scramble(word), word, answers);
	}

	@Nullable
	private GeneratedQuestion buildDexEntry() {
		if (dexEntries.isEmpty()) {
			return null;
		}

		List<String> pokemonWithEntries = new ArrayList<>(dexEntries.keySet());
		String pokemon = RandomUtils.getRandom(pokemonWithEntries);
		String entry = dexEntries.get(pokemon);
		String normalizedPokemon = AnswerNormalizer.normalize(pokemon);

		Set<String> answers = new HashSet<>();
		answers.add(normalizedPokemon);

		return new GeneratedQuestion(QuestionType.DEX_ENTRY.getKey(), entry, pokemon, answers);
	}

	@Nullable
	private GeneratedQuestion buildPokemonType() {
		if (pokemonTypes.isEmpty()) {
			return null;
		}

		List<String> pokemonList = new ArrayList<>(pokemonTypes.keySet());
		String pokemon = RandomUtils.getRandom(pokemonList);
		List<String> types = pokemonTypes.get(pokemon);

		List<String> normalizedTypes = new ArrayList<>();
		for (String type : types) {
			normalizedTypes.add(AnswerNormalizer.normalize(type));
		}
		Set<String> answers = new HashSet<>();
		answers.add(String.join(" ", normalizedTypes));
		answers.addAll(normalizedTypes);

		return new GeneratedQuestion(QuestionType.POKEMON_TYPE.getKey(), pokemon, String.join("/", types), answers);
	}

	@Nullable
	private GeneratedQuestion buildTypePokemon() {
		if (pokemonTypes.isEmpty()) {
			return null;
		}

		List<String> allTypes = pokemonTypes.values().stream()
			.flatMap(List::stream)
			.distinct()
			.collect(Collectors.toList());

		String chosenType = RandomUtils.getRandom(allTypes);
		String normalizedChosenType = AnswerNormalizer.normalize(chosenType);

		List<String> matchingPokemon = new ArrayList<>();
		Set<String> answers = new HashSet<>();
		for (Map.Entry<String, List<String>> entry : pokemonTypes.entrySet()) {
			for (String type : entry.getValue()) {
				if (AnswerNormalizer.normalize(type).equals(normalizedChosenType)) {
					addPokemonAnswers(answers, entry.getKey());
					matchingPokemon.add(entry.getKey());
					break;
				}
			}
		}

		String displayAnswer = matchingPokemon.isEmpty() ? chosenType : RandomUtils.getRandom(matchingPokemon);
		return new GeneratedQuestion(QuestionType.TYPE_POKEMON.getKey(), chosenType, displayAnswer, answers);
	}

	@Nullable
	private GeneratedQuestion buildPokemonAbility() {
		if (pokemonAbilities.isEmpty()) {
			return null;
		}

		List<String> pokemonWithAbilities = new ArrayList<>(pokemonAbilities.keySet());
		String pokemon = RandomUtils.getRandom(pokemonWithAbilities);
		List<String> abilities = pokemonAbilities.get(pokemon);

		Set<String> answers = new HashSet<>();
		for (String ability : abilities) {
			answers.add(AnswerNormalizer.normalize(ability));
		}

		return new GeneratedQuestion(QuestionType.POKEMON_ABILITY.getKey(), pokemon, abilities.getFirst(), answers);
	}

	@Nullable
	private GeneratedQuestion buildAbilityPokemon() {
		if (abilityPokemon.isEmpty()) {
			return null;
		}

		List<String> abilities = new ArrayList<>(abilityPokemon.keySet());
		String chosenAbility = RandomUtils.getRandom(abilities);

		Set<String> answers = new HashSet<>();
		for (String pokemon : abilityPokemon.get(chosenAbility)) {
			addPokemonAnswers(answers, pokemon);
		}

		List<String> pokemonList = abilityPokemon.get(chosenAbility);
		return new GeneratedQuestion(QuestionType.ABILITY_POKEMON.getKey(), chosenAbility, pokemonList.getFirst(), answers);
	}

	@Nullable
	private GeneratedQuestion buildPokemonForm() {
		if (pokemonForms.isEmpty()) {
			return null;
		}

		List<String> forms = new ArrayList<>(pokemonForms.keySet());
		String chosenForm = RandomUtils.getRandom(forms);

		Set<String> answers = new HashSet<>();
		for (String pokemon : pokemonForms.get(chosenForm)) {
			addPokemonAnswers(answers, pokemon);
		}

		List<String> pokemonList = pokemonForms.get(chosenForm);
		return new GeneratedQuestion(QuestionType.POKEMON_FORM.getKey(), chosenForm, pokemonList.getFirst(), answers);
	}

	@Nullable
	private GeneratedQuestion buildEggGroupPokemon() {
		if (eggGroupPokemon.isEmpty()) {
			return null;
		}

		List<String> groups = new ArrayList<>(eggGroupPokemon.keySet());
		String chosenGroup = RandomUtils.getRandom(groups);

		Set<String> answers = new HashSet<>();
		for (String pokemon : eggGroupPokemon.get(chosenGroup)) {
			addPokemonAnswers(answers, pokemon);
		}

		List<String> pokemonList = eggGroupPokemon.get(chosenGroup);
		return new GeneratedQuestion(QuestionType.EGG_GROUP_POKEMON.getKey(), chosenGroup, pokemonList.getFirst(), answers);
	}

	private static void addPokemonAnswers(Set<String> answers, String pokemonName) {
		answers.add(AnswerNormalizer.normalize(pokemonName));
		if (pokemonName.contains("♀") || pokemonName.contains("♂")) {
			answers.add(AnswerNormalizer.normalize(pokemonName.replace("♀", "").replace("♂", "").trim()));
		}
	}

	private String scramble(String word) {
		// Split on any whitespace (handles non-breaking spaces and other variants)
		String[] parts = word.trim().split("\\s+");
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				result.append(" ");
			}
			result.append(scramblePart(parts[i]));
		}
		return result.toString();
	}

	private String scramblePart(String word) {
		// Only shuffle alphanumeric characters; keep punctuation anchored in place.
		// This ensures the visible letter count per word always matches the answer.
		char[] chars = word.toCharArray();
		List<Integer> letterPositions = new ArrayList<>();
		List<Character> letters = new ArrayList<>();

		for (int i = 0; i < chars.length; i++) {
			if (Character.isLetterOrDigit(chars[i])) {
				letterPositions.add(i);
				letters.add(chars[i]);
			}
		}

		if (letters.size() <= 1) {
			return word;
		}

		String scrambled;
		int attempts = 0;

		do {
			Collections.shuffle(letters);

			char[] result = chars.clone();
			for (int i = 0; i < letterPositions.size(); i++) {
				result[letterPositions.get(i)] = letters.get(i);
			}

			scrambled = new String(result);
			attempts++;
		} while (scrambled.equalsIgnoreCase(word) && attempts < 10);

		return scrambled;
	}

}
