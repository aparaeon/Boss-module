package gg.mmorealms.module.breeding.backend.fabric.utils;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.*;
import com.raduvoinea.utils.generic.RandomUtils;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.config.ShinyMethod;
import gg.mmorealms.module.breeding.backend.fabric.dto.BreedingPair;
import gg.mmorealms.module.breeding.backend.fabric.dto.ParentData;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BreedingUtils {

    private BreedingUtils() {
    }

    @Nullable
    public static Pokemon breedRandomPair(List<Pokemon> pokemons) {
        List<BreedingPair> compatible = getCompatiblePairs(pokemons);
        if (compatible.isEmpty()) {
            return null;
        }

        BreedingPair parents = RandomUtils.getRandom(compatible);
        return breed(parents);
    }

    @Nullable
    public static Pokemon breed(Pokemon pokemon1, Pokemon pokemon2) {
        BreedingPair parents = new BreedingPair(pokemon1, pokemon2);
        return breed(parents);
    }

    @Nullable
    public static Pokemon breed(BreedingPair parents) {
        if (!parents.isCompatible()) {
            return null;
        }

        PokemonProperties pokemonProperties = getOffspringProperties(parents);
        if (pokemonProperties == null) {
            return null;
        }

        return createPokemon(pokemonProperties);
    }

    public static Pokemon createPokemon(PokemonProperties properties) {
        // Don't use properties.create()
        // to avoid Cobblemon's roll for shiny
        Pokemon pokemon = new Pokemon();
        properties.apply(pokemon);
        pokemon.initialize();

        forceBenchedMoves(pokemon, properties);

        return pokemon;
    }

    /**
     * No matter how many moves we assign in PokemonProperties,
     * Cobblemon will only apply up to 4, so we need to add the rest ourselves
     */
    public static void forceBenchedMoves(Pokemon pokemon, PokemonProperties properties) {
        if (properties.getMoves() == null) {
            return;
        }

        Set<String> desired = new HashSet<>(properties.getMoves());
        for (String moveName : desired) {
            MoveTemplate template = Moves.INSTANCE.getByName(moveName);

            if (template != null && pokemon.getMoveSet().getMoves().stream().noneMatch(move -> move.getTemplate() == template)) {
                pokemon.getBenchedMoves().add(new BenchedMove(template, 0));
            }
        }
    }

    @Nullable
    public static PokemonProperties getOffspringProperties(BreedingPair parents) {
        if (!parents.isCompatible()) {
            return null;
        }

        PokemonProperties offspringProperties = new PokemonProperties();
        FormData offspringFormData = parents.getOffspringFormData();

        String form = offspringFormData.formOnlyShowdownId();
        IVs ivs = getOffspringIVs(parents);
        List<String> moves = getOffspringMoveNames(parents);
        boolean shiny = getOffspringShiny(parents);
        String nature = getOffspringNatureName(parents);
        String ability = getOffspringAbilityName(parents);
        String species = offspringFormData.species.showdownId();
        Set<String> aspects = new HashSet<>(offspringFormData.getAspects());
        String pokeBall = getOffspringPokeBallName(parents);

        offspringProperties.setForm(form);
        offspringProperties.setLevel(1);
        offspringProperties.setFriendship(120);
        offspringProperties.setIvs(ivs);
        offspringProperties.setShiny(shiny);
        offspringProperties.setMoves(moves);
        offspringProperties.setNature(nature);
        offspringProperties.setAbility(ability);
        offspringProperties.setSpecies(species);
        offspringProperties.setAspects(aspects);
        offspringProperties.setPokeball(pokeBall);

        forceFormAspects(offspringProperties, offspringFormData);

        return offspringProperties;
    }

    /**
     * Even if to set FormData of PokemonProperties,
     * Cobblemon actually won't apply it, so we need to do some string shenanigans
     * and add aspects as custom properties
     */
    public static void forceFormAspects(PokemonProperties properties, FormData formData) {
        for (String aspect : formData.getAspects()) {
            if (!formData.formOnlyShowdownId().equals(properties.getForm())) {
                continue;
            }

            // Enable regional form
            properties.getCustomProperties().add(new FlagSpeciesFeature(aspect, true));

            // Regional form
            String region = aspect.contains("-")
                    ? aspect.substring(aspect.lastIndexOf('-') + 1)
                    : aspect;
            properties.getCustomProperties().add(new StringSpeciesFeature("region_bias", region));

            // Fich stripes
            String stripes = aspect.endsWith("striped")
                    ? aspect.substring(0, aspect.length() - "striped".length())
                    : aspect;
            properties.getCustomProperties().add(new StringSpeciesFeature("fish_stripes", stripes));
        }
    }

    public static final Map<String, List<String>> CROSS_SPECIES = Map.of(
            "nidoranf", List.of("nidoranf", "nidoranm"),
            "nidoranm", List.of("nidoranf", "nidoranm"),
            "volbeat", List.of("volbeat", "illumise"),
            "illumise", List.of("volbeat", "illumise"),

            "manaphy", List.of("phione")
    );

    public static final Map<String, Integer> EVOLUTION_FORM = Map.of(
            "sirfetchd", 1,
            "cursola", 1,
            "obstagoon", 1,
            "runerigus", 1,
            "clodsire", 1,
            "overqwil", 1,
            "sneasler", 1,

            "perrserker", 2,
            "basculegion", 2
    );

    public static FormData getOffspringFormData(Pokemon father, Pokemon mother) {

        Species fatherSpecies = father.getSpecies(),
                motherSpecies = mother.getSpecies();

        Species fatherPreSpecies = getPreEvoSpecies(father),
                motherPreSpecies = getPreEvoSpecies(mother);

        boolean fatherHasEverstone = father.heldItem().is(CobblemonItems.EVERSTONE),
                motherHasEverstone = mother.heldItem().is(CobblemonItems.EVERSTONE);

        Pokemon parent;

        if (fatherPreSpecies != motherPreSpecies) {
            // Only the same evolutionary line can pass it's form
            // (offspring inherits mother species by default, so we can compare father != mother)
            parent = mother;
        } else if (fatherHasEverstone && motherHasEverstone) {
            // If both hold everstone - random
            parent = ThreadLocalRandom.current().nextBoolean() ? mother : father;
        } else {
            // If father holds everstone - father form
            // Else, either mother holds it, or mother by default
            parent = fatherHasEverstone ? father : mother;
        }

        Species parentSpecies = (parent == father) ? fatherSpecies : motherSpecies;
        Species offspringSpecies = (parent == father) ? fatherPreSpecies : motherPreSpecies;
        offspringSpecies = handleCrossSpecies(offspringSpecies);

        FormData evolutionForm = handleEvolutionForm(offspringSpecies, parentSpecies);
        if (evolutionForm != null) {
            return evolutionForm;
        }

        FormData parentForm = parent.getForm();

        Optional<FormData> forms = offspringSpecies.getForms().stream().filter(formData ->
                        formData.formOnlyShowdownId().contains(parentForm.formOnlyShowdownId()))
                .findFirst();

        return forms.isPresent()
                ? forms.get()
                : offspringSpecies.getStandardForm();
    }

    @Nullable
    public static FormData handleEvolutionForm(Species offspringSpecies, Species parentSpecies) {
        Integer index = EVOLUTION_FORM.get(parentSpecies.toString());

        if (index == null) {
            return null;
        }

        return offspringSpecies.getForms().get(index);
    }

    public static Species handleCrossSpecies(Species parentSpecies) {
        Species resultSpecies = parentSpecies;
        List<String> specialSpeciesNames = CROSS_SPECIES.get(parentSpecies.toString());

        if (specialSpeciesNames != null) {
            String chosenSpeciesName = RandomUtils.getRandom(specialSpeciesNames);
            resultSpecies = PokemonSpecies.INSTANCE.getByName(chosenSpeciesName);
        }

        return resultSpecies;
    }

    /* --------- Ability --------- */

    private record AbilityInfo(Priority priority, int index) {
    }

    @Nullable
    public static String getOffspringAbilityName(BreedingPair parents) {
        FormData offspringFormData = parents.getOffspringFormData();

        AbilityTemplate abilityTemplate = getOffspringAbilityTemplate(offspringFormData, parents);
        if (abilityTemplate == null) {
            return null;
        }

        return abilityTemplate.getName();
    }

    @Nullable
    public static AbilityTemplate getOffspringAbilityTemplate(FormData offspringFormData, BreedingPair parents) {
        if (!parents.isCompatible()) {
            return null;
        }

        Pokemon mother = parents.getMother();
        Map<Priority, List<PotentialAbility>> abilityMapping = offspringFormData.getAbilities().getMapping();

        // Find mother's ability priority and index

        // Cobblemon is broken in regard to Hidden Abilities.
        // Pokemon with hidden ability returns priority as LOWEST, even though it should be LOW
        // and index of -1, which is completely wrong.
        // So we need to find ability manually
        AbilityInfo motherAbilityInfo = findMotherAbilityInfo(mother.getAbility(), abilityMapping);

        // Get the inherited ability
        PotentialAbility inheritedAbility = getInheritedAbility(motherAbilityInfo, abilityMapping);

        // Build list of alternative abilities
        List<PotentialAbility> alternativeAbilities = buildAlternativeAbilities(
                inheritedAbility, motherAbilityInfo.priority, abilityMapping);

        // Select final ability based on inheritance chance
        PotentialAbility selectedAbility = selectFinalAbility(
                inheritedAbility, alternativeAbilities, motherAbilityInfo.priority);

        return selectedAbility.getTemplate();
    }

    private static AbilityInfo findMotherAbilityInfo(Ability motherAbility,
                                                     Map<Priority, List<PotentialAbility>> abilityMapping) {
        for (var entry : abilityMapping.entrySet()) {
            Priority priority = entry.getKey();
            List<PotentialAbility> abilities = entry.getValue();

            for (int i = 0; i < abilities.size(); i++) {
                if (abilities.get(i).getTemplate().equals(motherAbility.getTemplate())) {
                    return new AbilityInfo(priority, i);
                }
            }
        }
        return new AbilityInfo(Priority.LOWEST, 0);
    }

    private static PotentialAbility getInheritedAbility(AbilityInfo motherInfo,
                                                        Map<Priority, List<PotentialAbility>> abilityMapping) {

        List<PotentialAbility> abilitiesForPriority = abilityMapping.getOrDefault(
                motherInfo.priority, abilityMapping.get(Priority.LOWEST));

        return (motherInfo.index < abilitiesForPriority.size())
                ? abilitiesForPriority.get(motherInfo.index)
                : abilitiesForPriority.getFirst();
    }

    private static List<PotentialAbility> buildAlternativeAbilities(PotentialAbility inheritedAbility,
                                                                    Priority motherPriority,
                                                                    Map<Priority, List<PotentialAbility>> abilityMapping) {
        List<PotentialAbility> alternatives = new ArrayList<>();

        for (var entry : abilityMapping.entrySet()) {
            Priority priority = entry.getKey();

            // Skip hidden abilities if mother doesn't have hidden ability
            if (priority == Priority.LOW && motherPriority != Priority.LOW) {
                continue;
            }

            entry.getValue().stream()
                    .filter(ability -> !ability.equals(inheritedAbility))
                    .forEach(alternatives::add);
        }

        return alternatives;
    }

    private static PotentialAbility selectFinalAbility(PotentialAbility inheritedAbility,
                                                       List<PotentialAbility> alternatives,
                                                       Priority motherPriority) {
        if (alternatives.isEmpty()) {
            return inheritedAbility;
        }

        float inheritanceChance = (motherPriority == Priority.LOW) ? 0.6f : 0.8f;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        return (random.nextFloat() < inheritanceChance)
                ? inheritedAbility
                : RandomUtils.getRandom(alternatives);
    }

    /* --------- Nature --------- */

    @Nullable
    public static String getOffspringNatureName(BreedingPair parents) {
        Nature nature = getOffspringNature(parents);
        if (nature == null) {
            return null;
        }

        return nature.getName().toString();
    }

    public static Nature getOffspringNature(BreedingPair parents) {
        if (!parents.isCompatible()) {
            return null;
        }

        List<Nature> parentNatures = new ArrayList<>();

        // If parent holds Everstone, we will pass its Nature
        parents.forEach(parent -> {
            if (parent.heldItem().is(CobblemonItems.EVERSTONE)) {
                parentNatures.add(parent.getNature());
            }
        });

        if (parentNatures.isEmpty()) {
            return Natures.INSTANCE.getRandomNature();
        }

        // If both parents hold Everstone - chose random parent's nature
        return RandomUtils.getRandom(parentNatures);
    }

    /* --------- Moves --------- */

    public static void applyMirrorHerb(List<Pokemon> pokemons) {
        for (int i = 0; i < pokemons.size(); i++) {
            Pokemon pokemon1 = pokemons.get(i);

            boolean holdsMirrorHerb = pokemon1.heldItem().is(CobblemonItems.MIRROR_HERB);
            if (!holdsMirrorHerb) {
                continue;
            }

            Species preEvoSpecies = getPreEvoSpecies(pokemon1);
            Set<MoveTemplate> allMoves = pokemon1.getAllAccessibleMoves();
            Set<MoveTemplate> possibleEggMoves = new HashSet<>(preEvoSpecies.getMoves().getEggMoves());

            // Remove already known moves
            possibleEggMoves.removeAll(allMoves);

            Set<MoveTemplate> eggMovesToInherit = new HashSet<>();

            for (int j = 0; j < pokemons.size(); j++) {
                if (i == j) {
                    continue; // Skip self
                }

                Pokemon pokemon2 = pokemons.get(j);
                Set<MoveTemplate> otherPokemonMoves = pokemon2.getAllAccessibleMoves();

                // Find the intersection of other Pokemon's moves with possible egg moves
                for (MoveTemplate move : otherPokemonMoves) {
                    if (possibleEggMoves.contains(move)) {
                        eggMovesToInherit.add(move);
                    }
                }
            }

            // Add inherited moves to the Pokemon with Mirror Herb
            eggMovesToInherit.forEach(moveTemplate -> {
                if (pokemon1.getMoveSet().hasSpace()) {
                    pokemon1.getMoveSet().add(moveTemplate.create());
                } else {
                    BenchedMove benchedMove = new BenchedMove(moveTemplate, 0);
                    pokemon1.getBenchedMoves().add(benchedMove);
                }
            });
        }
    }


    @Nullable
    public static List<String> getOffspringMoveNames(BreedingPair parents) {
        List<MoveTemplate> moveTemplates = getOffspringMoveTemplates(parents);
        if (moveTemplates == null) {
            return null;
        }

        return moveTemplates.stream()
                .map(MoveTemplate::getName)
                .toList();
    }

    public static List<MoveTemplate> getOffspringMoveTemplates(BreedingPair parents) {
        if (!parents.isCompatible()) {
            return null;
        }

        LinkedHashSet<MoveTemplate> offspringMoveTemplates = new LinkedHashSet<>();
        Species offspringSpecies = parents.getOffspringFormData().species;

        // Always add level 1 moves of Species
        offspringMoveTemplates.addAll(offspringSpecies.getMoves().getLevelUpMovesUpTo(1));

        // Add level up moves that both parents know
        offspringMoveTemplates.addAll(parents.offspringLevelUpMoves());

        // Add Egg moves from parents
        offspringMoveTemplates.addAll(parents.offspringEggMoves());

        // Add moves that have special requirements
        offspringMoveTemplates.addAll(getSpecialOffspringMoveTemplates(parents));

        return new ArrayList<>(offspringMoveTemplates);
    }

    public static Set<MoveTemplate> getSpecialOffspringMoveTemplates(BreedingPair parents) {
        Set<MoveTemplate> offspringMoveTemplates = new LinkedHashSet<>();

        Species offspringSpecies = parents.getOffspringFormData().species;
        if (offspringSpecies.toString().equals("pichu")) {
            boolean hasLightBall = parents.toList().stream().anyMatch(pokemon ->
                    pokemon.heldItem().getItem() == CobblemonItems.LIGHT_BALL);

            if (hasLightBall) {
                MoveTemplate voltTackle = Moves.INSTANCE.getByName("volttackle");
                offspringMoveTemplates.add(voltTackle);
            }
        }

        return offspringMoveTemplates;
    }

    /* --------- IVs --------- */

    public static final Map<Item, Stats> POWER_ITEM_STATS = Map.of(
            CobblemonItems.POWER_WEIGHT, Stats.HP,
            CobblemonItems.POWER_BRACER, Stats.ATTACK,
            CobblemonItems.POWER_LENS, Stats.SPECIAL_ATTACK,
            CobblemonItems.POWER_BELT, Stats.DEFENCE,
            CobblemonItems.POWER_BAND, Stats.SPECIAL_DEFENCE,
            CobblemonItems.POWER_ANKLET, Stats.SPEED
    );

    @Nullable
    public static Stats powerItemToStats(Item item) {
        return POWER_ITEM_STATS.get(item);
    }

    @Nullable
    public static IVs getOffspringIVs(BreedingPair parents) {
        if (!parents.isCompatible()) {
            return null;
        }

        IVs offspringIVs = IVs.createRandomIVs(0);

        // Determine breeding mechanics
        boolean hasDestinyKnot = parents.hasItem(CobblemonItems.DESTINY_KNOT);
        int inheritedStatsCount = hasDestinyKnot ? 5 : 3;

        // Handle power items (guaranteed stat inheritance)
        Map<Stats, Pokemon> guaranteedStats = collectGuaranteedStats(parents);
        applyGuaranteedStats(offspringIVs, guaranteedStats);

        // Handle remaining random stat inheritance
        Set<Stat> usedStats = new HashSet<>(guaranteedStats.keySet());
        int remainingSlots = inheritedStatsCount - guaranteedStats.size();
        inheritRandomStats(parents, offspringIVs, usedStats, remainingSlots);

        return offspringIVs;
    }

    private static Map<Stats, Pokemon> collectGuaranteedStats(BreedingPair parents) {
        Map<Stats, Pokemon> guaranteedStats = new HashMap<>();

        parents.forEach(pokemon -> {
            Stats powerStat = powerItemToStats(pokemon.heldItem().getItem());
            if (powerStat != null) {
                // If same power item exists, randomly choose between existing and current parent
                guaranteedStats.merge(powerStat, pokemon,
                        (existing, current) -> ThreadLocalRandom.current().nextBoolean() ? existing : current);
            }
        });

        return guaranteedStats;
    }

    private static void applyGuaranteedStats(IVs offspringIVs, Map<Stats, Pokemon> guaranteedStats) {
        guaranteedStats.forEach((stat, parent) -> {
            Integer parentIV = parent.getIvs().get(stat);
            if (parentIV != null) {
                offspringIVs.set(stat, parentIV);
            }
        });
    }

    private static void inheritRandomStats(BreedingPair parents,
                                           IVs offspringIVs,
                                           Set<Stat> usedStats,
                                           int remainingSlots) {

        Set<Stat> availableStats = new HashSet<>(Stats.Companion.getPERMANENT());
        availableStats.removeAll(usedStats);

        for (int i = 0; i < remainingSlots && !availableStats.isEmpty(); i++) {
            Stat chosenStat = RandomUtils.getRandom(new ArrayList<>(availableStats));
            Pokemon randomParent = parents.random();

            Integer parentIV = randomParent.getIvs().get(chosenStat);
            if (parentIV != null) {
                offspringIVs.set(chosenStat, parentIV);
            }

            availableStats.remove(chosenStat);
        }
    }

    /* --------- Pokeball --------- */

    @Nullable
    public static String getOffspringPokeBallName(BreedingPair parents) {
        PokeBall pokeBall = getOffspringPokeBall(parents);
        if (pokeBall == null) {
            return null;
        }

        return pokeBall.getName().toString();
    }

    @Nullable
    public static PokeBall getOffspringPokeBall(BreedingPair parents) {
        if (!parents.isCompatible()) {
            return null;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        Pokemon father = parents.getFather();
        Pokemon mother = parents.getMother();

        // Mother (non-ditto parent) ball as default
        // If father is same species of mother - 50/50 for the chosen ball
        PokeBall offspringPokeBall = (father.getSpecies() == mother.getSpecies() && random.nextBoolean())
                ? father.getCaughtBall()
                : mother.getCaughtBall();

        // In games if Pokemon is MasterBall or CherishBall - it is treated as PokeBall
        if (offspringPokeBall == PokeBalls.INSTANCE.getMASTER_BALL()
                || offspringPokeBall == PokeBalls.INSTANCE.getCHERISH_BALL()) {
            offspringPokeBall = PokeBalls.INSTANCE.getPOKE_BALL();
        }

        return offspringPokeBall;
    }

    /* --------- Shiny --------- */

    public static boolean getOffspringShiny(BreedingPair parents) {
        Integer shinyOdds = getOffspringShinyOdds(parents);
        if (shinyOdds == null) {
            return false;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        return shinyOdds <= 1 || random.nextInt(shinyOdds) == 0;
    }

    @Nullable
    public static Integer getOffspringShinyOdds(BreedingPair parents) {
        if (!parents.isCompatible()) {
            return null;
        }

        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        float shinyOdds = config.breeding.baseShinyOdds;
        float rolls = 1f;

        if (hasShinyParent(parents)) {
            rolls += getShinyRoll(ShinyMethod.CRYSTAL);
        }

        if (hasDifferentOriginalTrainer(parents)) {
            rolls += getShinyRoll(ShinyMethod.MASUDA);
        }

        shinyOdds /= rolls;

        return (int) shinyOdds;
    }

    private static boolean hasShinyParent(BreedingPair parents) {
        return parents.getFather().getShiny() || parents.getMother().getShiny();
    }

    private static boolean hasDifferentOriginalTrainer(BreedingPair parents) {
        String otFather = parents.getFather().getOriginalTrainer();
        String otMother = parents.getMother().getOriginalTrainer();
        if (otFather == null || otMother == null) {
            return false;
        }

        return !otFather.equals(otMother);
    }

    private static float getShinyRoll(ShinyMethod method) {
        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        return config.breeding.shinyBreedingRolls.getOrDefault(method, 0f);
    }

    /* ---------- Helpers ---------- */

    public static Species getPreEvoSpecies(BreedingPair parents) {
        return getPreEvoSpecies(parents.getMother());
    }

    public static Species getPreEvoSpecies(Pokemon pokemon) {
        return getPreEvoSpecies(pokemon.getSpecies());
    }

    public static Species getPreEvoSpecies(Species species) {
        while (species.getPreEvolution() != null) {
            species = species.getPreEvolution().getSpecies();
        }

        return species;
    }

    public static List<BreedingPair> getCompatiblePairs(List<Pokemon> pokemons) {
        List<BreedingPair> breedingPairs = new ArrayList<>();

        for (int i = 0; i < pokemons.size(); i++) {
            for (int j = i + 1; j < pokemons.size(); j++) {
                BreedingPair breedingPair = new BreedingPair(pokemons.get(i), pokemons.get(j));

                if (breedingPair.isCompatible()) {
                    breedingPairs.add(breedingPair);
                }
            }
        }

        return breedingPairs;
    }

    public static boolean isCompatiblePair(Pokemon pokemon1, Pokemon pokemon2) {
        boolean isBreedablePokemon1 = isBreedablePokemon(pokemon1);
        if (!isBreedablePokemon1) {
            return false;
        }

        boolean isBreedablePokemon2 = isBreedablePokemon(pokemon2);
        if (!isBreedablePokemon2) {
            return false;
        }

        boolean isDittoPokemon1 = isDitto(pokemon1),
                isDittoPokemon2 = isDitto(pokemon2);

        // Can't breed two dittos
        if (isDittoPokemon1 && isDittoPokemon2) {
            return false;
        }

        // Can breed if one parent is ditto
        if (isDittoPokemon1 || isDittoPokemon2) {
            return true;
        }

        // Non-ditto has at least one matching egg group
        boolean hasSameEggGroup = hasSameEggGroup(pokemon1, pokemon2);
        if (!hasSameEggGroup) {
            return false;
        }

        Gender genderPokemon1 = pokemon1.getGender();
        Gender genderPokemon2 = pokemon2.getGender();
        // Genderless non-Ditto can only breed with Ditto,
        // but here neither is Ditto, so any genderless fails
        if (genderPokemon1 == Gender.GENDERLESS ||
                genderPokemon2 == Gender.GENDERLESS) {
            return false;
        }

        // Can't breed same non-genderless genders
        if (genderPokemon1 == genderPokemon2) {
            return false;
        }

        return true;
    }

    public static boolean hasSameEggGroup(Pokemon pokemon1, Pokemon pokemon2) {
        return !Collections.disjoint(
                pokemon1.getSpecies().getEggGroups(),
                pokemon2.getSpecies().getEggGroups());
    }

    public static boolean isBreedablePokemon(Pokemon pokemon) {
        return !hasEggGroup(pokemon, EggGroup.UNDISCOVERED)
                && !pokemon.getForcedAspects().contains(IPokemon.NEUTERED_KEY);
    }

    public static boolean isDitto(Pokemon pokemon) {
        return hasEggGroup(pokemon, EggGroup.DITTO);
    }

    public static boolean hasEggGroup(Pokemon pokemon, EggGroup eggGroup) {
        return pokemon.getSpecies().getEggGroups().contains(eggGroup);
    }

    public static ParentData toParentData(Pokemon pokemon) {
        return new ParentData(pokemon.getSpecies().getName(), pokemon.getGender());
    }

}
