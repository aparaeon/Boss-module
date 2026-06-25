package gg.mmorealms.module.boss.backend.fabric.manager;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokemon.moves.Learnset;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Species;
import gg.mmorealms.module.boss.common.BossTier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BossMovesetPlanner {

	private BossMovesetPlanner() {
	}

	public record PlayerSnapshot(@NotNull List<ElementalType> leadTypes) {
	}

	private static final double STAB_MULTIPLIER = 1.5;
	private static final int MIN_RELIABLE_ACCURACY = 80;

	private static final Set<String> SELF_FAINT_MOVES = Set.of(
			"explosion", "selfdestruct", "mindblown", "mistyexplosion", "finalgambit", "memento", "healingwish", "lunardance");

	private static final List<String> RECOVERY_MOVES = List.of(
			"recover", "roost", "slackoff", "softboiled", "milkdrink", "healorder", "rest");

	private static final List<String> LOW_TIER_STATUS = List.of("leechseed", "thunderwave", "yawn");

	private static final List<String> CRIPPLE_STATUS = List.of(
			"willowisp", "thunderwave", "toxic", "leechseed", "glare", "nuzzle", "yawn");

	private static final List<String> PHYSICAL_SETUP = List.of(
			"shellsmash", "swordsdance", "dragondance", "victorydance", "shiftgear", "bulkup", "tidyup", "coil");
	private static final List<String> SPECIAL_SETUP = List.of(
			"tailglow", "nastyplot", "quiverdance", "geomancy", "calmmind", "takeheart");

	public static @NotNull List<String> planStatic(@NotNull BossTier tier, @Nullable Species species, int level) {
		return build(tier, species, level, null, 0);
	}

	public static @NotNull List<String> planAdaptive(@NotNull BossTier tier, @Nullable Species species, int level,
	                                                 @NotNull PlayerSnapshot player) {
		return build(tier, species, level, player, adaptiveSlotCount(tier));
	}

	private static @NotNull List<String> build(@NotNull BossTier tier, @Nullable Species species, int level,
	                                           @Nullable PlayerSnapshot player, int adaptiveSlots) {
		if (species == null) {
			return List.of();
		}

		Set<ElementalType> speciesTypes = new HashSet<>();
		for (ElementalType type : species.getTypes()) {
			speciesTypes.add(type);
		}

		List<MoveTemplate> pool = buildPool(species, level);
		Set<String> poolNames = new HashSet<>();
		for (MoveTemplate move : pool) {
			poolNames.add(move.getName());
		}

		List<MoveTemplate> damaging = new ArrayList<>(pool.stream()
				.filter(BossMovesetPlanner::isReliableAttack)
				.sorted((moveA, moveB) -> Double.compare(score(moveB, speciesTypes), score(moveA, speciesTypes)))
				.toList());

		List<String> moves = new ArrayList<>(4);
		Set<String> used = new HashSet<>();
		Set<ElementalType> usedTypes = new HashSet<>();

		MoveTemplate stab = firstWhere(damaging, used, move -> speciesTypes.contains(move.getElementalType()));
		if (stab == null) {
			stab = firstWhere(damaging, used, move -> true);
		}
		addDamage(moves, used, usedTypes, stab);

		addCoverageSlot(moves, used, usedTypes, damaging, adaptiveSlots >= 2 ? player : null);
		addCoverageSlot(moves, used, usedTypes, damaging, adaptiveSlots >= 1 ? player : null);

		String utility = pickUtility(tier, pool, poolNames, used);
		if (utility == null) {
			MoveTemplate fallback = firstWhere(damaging, used, move -> true);
			if (fallback != null) {
				moves.add(fallback.getName());
				used.add(fallback.getName());
			}
		} else {
			moves.add(utility);
			used.add(utility);
		}

		return moves;
	}

	private static void addCoverageSlot(@NotNull List<String> moves, @NotNull Set<String> used,
	                                    @NotNull Set<ElementalType> usedTypes, @NotNull List<MoveTemplate> damaging,
	                                    @Nullable PlayerSnapshot player) {
		MoveTemplate move = null;
		if (player != null && !player.leadTypes().isEmpty()) {
			move = firstWhere(damaging, used, candidate -> !usedTypes.contains(candidate.getElementalType())
					&& leadEffectiveness(candidate.getElementalType(), player.leadTypes()) > 1.0);
		}
		if (move == null) {
			move = firstWhere(damaging, used, candidate -> !usedTypes.contains(candidate.getElementalType()));
		}
		if (move == null) {
			move = firstWhere(damaging, used, candidate -> true);
		}
		addDamage(moves, used, usedTypes, move);
	}

	private static int adaptiveSlotCount(@NotNull BossTier tier) {
		if (isTopTier(tier)) {
			return 2;
		}
		if (tier == BossTier.RARE || tier == BossTier.ULTRA_RARE) {
			return 1;
		}
		return 0;
	}

	private static double leadEffectiveness(@NotNull ElementalType attackType, @NotNull List<ElementalType> leadTypes) {
		double multiplier = 1.0;
		Map<String, Double> row = TYPE_CHART.get(attackType.getName());
		if (row == null) {
			return 1.0;
		}
		for (ElementalType defendType : leadTypes) {
			multiplier *= row.getOrDefault(defendType.getName(), 1.0);
		}
		return multiplier;
	}

	private static final Map<String, Map<String, Double>> TYPE_CHART = buildTypeChart();

	private static Map<String, Map<String, Double>> buildTypeChart() {
		Map<String, Map<String, Double>> chart = new HashMap<>();
		chart.put("normal", chartRow("rock:0.5", "ghost:0", "steel:0.5"));
		chart.put("fire", chartRow("fire:0.5", "water:0.5", "grass:2", "ice:2", "bug:2", "rock:0.5", "dragon:0.5", "steel:2"));
		chart.put("water", chartRow("fire:2", "water:0.5", "grass:0.5", "ground:2", "rock:2", "dragon:0.5"));
		chart.put("electric", chartRow("water:2", "electric:0.5", "grass:0.5", "ground:0", "flying:2", "dragon:0.5"));
		chart.put("grass", chartRow("fire:0.5", "water:2", "grass:0.5", "poison:0.5", "ground:2", "flying:0.5", "bug:0.5", "rock:2", "dragon:0.5", "steel:0.5"));
		chart.put("ice", chartRow("fire:0.5", "water:0.5", "grass:2", "ice:0.5", "ground:2", "flying:2", "dragon:2", "steel:0.5"));
		chart.put("fighting", chartRow("normal:2", "ice:2", "poison:0.5", "flying:0.5", "psychic:0.5", "bug:0.5", "rock:2", "ghost:0", "dark:2", "steel:2", "fairy:0.5"));
		chart.put("poison", chartRow("grass:2", "poison:0.5", "ground:0.5", "rock:0.5", "ghost:0.5", "steel:0", "fairy:2"));
		chart.put("ground", chartRow("fire:2", "electric:2", "grass:0.5", "poison:2", "flying:0", "bug:0.5", "rock:2", "steel:2"));
		chart.put("flying", chartRow("electric:0.5", "grass:2", "fighting:2", "bug:2", "rock:0.5", "steel:0.5"));
		chart.put("psychic", chartRow("fighting:2", "poison:2", "psychic:0.5", "dark:0", "steel:0.5"));
		chart.put("bug", chartRow("fire:0.5", "grass:2", "fighting:0.5", "poison:0.5", "flying:0.5", "psychic:2", "ghost:0.5", "dark:2", "steel:0.5", "fairy:0.5"));
		chart.put("rock", chartRow("fire:2", "ice:2", "fighting:0.5", "ground:0.5", "flying:2", "bug:2", "steel:0.5"));
		chart.put("ghost", chartRow("normal:0", "psychic:2", "ghost:2", "dark:0.5"));
		chart.put("dragon", chartRow("dragon:2", "steel:0.5", "fairy:0"));
		chart.put("dark", chartRow("fighting:0.5", "psychic:2", "ghost:2", "dark:0.5", "fairy:0.5"));
		chart.put("steel", chartRow("fire:0.5", "water:0.5", "electric:0.5", "ice:2", "rock:2", "steel:0.5", "fairy:2"));
		chart.put("fairy", chartRow("fire:0.5", "fighting:2", "poison:0.5", "dragon:2", "dark:2", "steel:0.5"));
		return chart;
	}

	private static Map<String, Double> chartRow(@NotNull String... entries) {
		Map<String, Double> row = new HashMap<>();
		for (String entry : entries) {
			int separatorIndex = entry.indexOf(':');
			row.put(entry.substring(0, separatorIndex), Double.parseDouble(entry.substring(separatorIndex + 1)));
		}
		return row;
	}

	private static @Nullable String pickUtility(@NotNull BossTier tier, @NotNull List<MoveTemplate> pool,
	                                            @NotNull Set<String> poolNames, @NotNull Set<String> used) {
		if (isTopTier(tier)) {
			String setup = firstLearnable(isPhysicalBias(pool) ? PHYSICAL_SETUP : SPECIAL_SETUP, poolNames, used);
			if (setup != null) {
				return setup;
			}
			String recovery = firstLearnable(RECOVERY_MOVES, poolNames, used);
			if (recovery != null) {
				return recovery;
			}
			return firstLearnable(CRIPPLE_STATUS, poolNames, used);
		}

		String recovery = firstLearnable(RECOVERY_MOVES, poolNames, used);
		if (recovery != null) {
			return recovery;
		}
		List<String> statusPool = isLowTier(tier) ? LOW_TIER_STATUS : CRIPPLE_STATUS;
		return firstLearnable(statusPool, poolNames, used);
	}

	private static boolean isLowTier(@NotNull BossTier tier) {
		return tier == BossTier.COMMON || tier == BossTier.UNCOMMON;
	}

	private static boolean isTopTier(@NotNull BossTier tier) {
		return tier == BossTier.LEGENDARY || tier == BossTier.MEGA || tier == BossTier.MYTHICAL;
	}

	private static @NotNull List<MoveTemplate> buildPool(@NotNull Species species, int level) {
		Learnset learnset = species.getMoves();
		Set<MoveTemplate> pool = new HashSet<>();
		pool.addAll(learnset.getLevelUpMovesUpTo(level));
		pool.addAll(learnset.getTmMoves());
		pool.removeIf(move -> SELF_FAINT_MOVES.contains(move.getName()));
		return new ArrayList<>(pool);
	}

	private static boolean isReliableAttack(@NotNull MoveTemplate move) {
		return move.getPower() > 0 && (move.getAccuracy() == 0 || move.getAccuracy() >= MIN_RELIABLE_ACCURACY);
	}

	private static double score(@NotNull MoveTemplate move, @NotNull Set<ElementalType> speciesTypes) {
		double accuracy = move.getAccuracy() == 0 ? 100 : move.getAccuracy();
		double stab = speciesTypes.contains(move.getElementalType()) ? STAB_MULTIPLIER : 1.0;
		return move.getPower() * accuracy * stab;
	}

	private static boolean isPhysicalBias(@NotNull List<MoveTemplate> pool) {
		double physicalPower = 0;
		double specialPower = 0;
		for (MoveTemplate move : pool) {
			if (move.getPower() <= 0) {
				continue;
			}
			String category = move.getDamageCategory().getName();
			if ("physical".equalsIgnoreCase(category)) {
				physicalPower += move.getPower();
			} else if ("special".equalsIgnoreCase(category)) {
				specialPower += move.getPower();
			}
		}
		return physicalPower >= specialPower;
	}

	private static @Nullable MoveTemplate firstWhere(@NotNull List<MoveTemplate> damaging, @NotNull Set<String> used,
	                                                 @NotNull java.util.function.Predicate<MoveTemplate> predicate) {
		for (MoveTemplate move : damaging) {
			if (!used.contains(move.getName()) && predicate.test(move)) {
				return move;
			}
		}
		return null;
	}

	private static void addDamage(@NotNull List<String> moves, @NotNull Set<String> used,
	                              @NotNull Set<ElementalType> usedTypes, @Nullable MoveTemplate move) {
		if (move == null) {
			return;
		}
		moves.add(move.getName());
		used.add(move.getName());
		usedTypes.add(move.getElementalType());
	}

	private static @Nullable String firstLearnable(@NotNull List<String> candidates, @NotNull Set<String> poolNames,
	                                               @NotNull Set<String> used) {
		for (String candidate : candidates) {
			if (poolNames.contains(candidate) && !used.contains(candidate)) {
				return candidate;
			}
		}
		return null;
	}
}
