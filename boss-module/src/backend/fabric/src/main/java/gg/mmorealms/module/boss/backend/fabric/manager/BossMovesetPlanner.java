package gg.mmorealms.module.boss.backend.fabric.manager;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokemon.moves.Learnset;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Species;
import gg.mmorealms.module.boss.common.BossTier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BossMovesetPlanner {

	private BossMovesetPlanner() {
	}

	public enum Role {
		WALL,
		SWEEPER,
		BRUISER
	}

	private static final double STAB_MULTIPLIER = 1.5;
	private static final double CATEGORY_BIAS = 1.3;
	private static final int MIN_RELIABLE_ACCURACY = 80;

	private static final Set<String> SELF_FAINT_MOVES = Set.of(
			"explosion", "selfdestruct", "mindblown", "mistyexplosion", "finalgambit", "memento", "healingwish", "lunardance");

	private static final List<String> RECOVERY_MOVES = List.of(
			"recover", "roost", "slackoff", "softboiled", "milkdrink", "healorder", "rest");

	private static final List<String> PROTECT_MOVES = List.of(
			"protect", "spikyshield", "banefulbunker", "kingsshield", "detect");

	private static final List<String> WALL_STATUS = List.of(
			"toxic", "leechseed", "willowisp", "thunderwave", "glare", "yawn");

	private static final List<String> LOW_TIER_STATUS = List.of("leechseed", "thunderwave", "yawn");

	private static final List<String> CRIPPLE_STATUS = List.of(
			"willowisp", "thunderwave", "toxic", "leechseed", "glare", "nuzzle", "yawn");

	private static final List<String> PHYSICAL_SETUP = List.of(
			"shellsmash", "swordsdance", "dragondance", "victorydance", "shiftgear", "bulkup", "tidyup", "coil");
	private static final List<String> SPECIAL_SETUP = List.of(
			"tailglow", "nastyplot", "quiverdance", "geomancy", "calmmind", "takeheart");

	public static @NotNull List<String> planStatic(@NotNull BossTier tier, @Nullable Species species, int level,
	                                               @NotNull Role role, boolean physical) {
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
				.sorted((moveA, moveB) -> Double.compare(score(moveB, speciesTypes, physical), score(moveA, speciesTypes, physical)))
				.toList());

		return switch (role) {
			case WALL -> planWall(damaging, poolNames, speciesTypes);
			case SWEEPER -> planSweeper(damaging, poolNames, speciesTypes, physical);
			case BRUISER -> planBruiser(tier, damaging, poolNames, speciesTypes, physical);
		};
	}

	private static @NotNull List<String> planBruiser(@NotNull BossTier tier, @NotNull List<MoveTemplate> damaging,
	                                                 @NotNull Set<String> poolNames, @NotNull Set<ElementalType> speciesTypes,
	                                                 boolean physical) {
		List<String> moves = new ArrayList<>(4);
		Set<String> used = new HashSet<>();
		Set<ElementalType> usedTypes = new HashSet<>();

		MoveTemplate stab = firstWhere(damaging, used, move -> speciesTypes.contains(move.getElementalType()));
		if (stab == null) {
			stab = firstWhere(damaging, used, move -> true);
		}
		addDamage(moves, used, usedTypes, stab);

		addCoverageSlot(moves, used, usedTypes, damaging);
		addCoverageSlot(moves, used, usedTypes, damaging);

		String utility = pickUtility(tier, poolNames, used, physical);
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

	private static @NotNull List<String> planSweeper(@NotNull List<MoveTemplate> damaging, @NotNull Set<String> poolNames,
	                                                 @NotNull Set<ElementalType> speciesTypes, boolean physical) {
		List<String> moves = new ArrayList<>(4);
		Set<String> used = new HashSet<>();
		Set<ElementalType> usedTypes = new HashSet<>();

		String setup = firstLearnable(physical ? PHYSICAL_SETUP : SPECIAL_SETUP, poolNames, used);
		if (setup != null) {
			moves.add(setup);
			used.add(setup);
		}

		MoveTemplate stab = firstWhere(damaging, used, move -> speciesTypes.contains(move.getElementalType()));
		if (stab == null) {
			stab = firstWhere(damaging, used, move -> true);
		}
		addDamage(moves, used, usedTypes, stab);

		while (moves.size() < 4) {
			MoveTemplate move = firstWhere(damaging, used, candidate -> !usedTypes.contains(candidate.getElementalType()));
			if (move == null) {
				move = firstWhere(damaging, used, candidate -> true);
			}
			if (move == null) {
				break;
			}
			addDamage(moves, used, usedTypes, move);
		}

		return moves;
	}

	private static @NotNull List<String> planWall(@NotNull List<MoveTemplate> damaging, @NotNull Set<String> poolNames,
	                                              @NotNull Set<ElementalType> speciesTypes) {
		List<String> moves = new ArrayList<>(4);
		Set<String> used = new HashSet<>();
		Set<ElementalType> usedTypes = new HashSet<>();

		addUtility(moves, used, firstLearnable(RECOVERY_MOVES, poolNames, used));
		addUtility(moves, used, firstLearnable(PROTECT_MOVES, poolNames, used));
		addUtility(moves, used, firstLearnable(WALL_STATUS, poolNames, used));

		if (moves.size() < 4) {
			MoveTemplate stab = firstWhere(damaging, used, move -> speciesTypes.contains(move.getElementalType()));
			if (stab == null) {
				stab = firstWhere(damaging, used, move -> true);
			}
			addDamage(moves, used, usedTypes, stab);
		}

		while (moves.size() < 4) {
			String status = firstLearnable(CRIPPLE_STATUS, poolNames, used);
			if (status != null) {
				addUtility(moves, used, status);
				continue;
			}
			MoveTemplate move = firstWhere(damaging, used, candidate -> true);
			if (move == null) {
				break;
			}
			addDamage(moves, used, usedTypes, move);
		}

		return moves;
	}

	private static void addUtility(@NotNull List<String> moves, @NotNull Set<String> used, @Nullable String move) {
		if (move == null || moves.size() >= 4) {
			return;
		}
		moves.add(move);
		used.add(move);
	}

	private static void addCoverageSlot(@NotNull List<String> moves, @NotNull Set<String> used,
	                                    @NotNull Set<ElementalType> usedTypes, @NotNull List<MoveTemplate> damaging) {
		MoveTemplate move = firstWhere(damaging, used, candidate -> !usedTypes.contains(candidate.getElementalType()));
		if (move == null) {
			move = firstWhere(damaging, used, candidate -> true);
		}
		addDamage(moves, used, usedTypes, move);
	}

	private static @Nullable String pickUtility(@NotNull BossTier tier, @NotNull Set<String> poolNames,
	                                            @NotNull Set<String> used, boolean physical) {
		if (isTopTier(tier)) {
			String setup = firstLearnable(physical ? PHYSICAL_SETUP : SPECIAL_SETUP, poolNames, used);
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

	private static double score(@NotNull MoveTemplate move, @NotNull Set<ElementalType> speciesTypes, boolean physical) {
		double accuracy = move.getAccuracy() == 0 ? 100 : move.getAccuracy();
		double stab = speciesTypes.contains(move.getElementalType()) ? STAB_MULTIPLIER : 1.0;
		double category = matchesCategory(move, physical) ? CATEGORY_BIAS : 1.0;
		return move.getPower() * accuracy * stab * category;
	}

	private static boolean matchesCategory(@NotNull MoveTemplate move, boolean physical) {
		String category = move.getDamageCategory().getName();
		return physical ? "physical".equalsIgnoreCase(category) : "special".equalsIgnoreCase(category);
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
