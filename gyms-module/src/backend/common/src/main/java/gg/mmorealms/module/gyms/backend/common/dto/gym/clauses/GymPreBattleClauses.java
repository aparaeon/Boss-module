package gg.mmorealms.module.gyms.backend.common.dto.gym.clauses;

import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class.PokemonClass;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
// TODO: Implement / Test for Pixelmon
public enum GymPreBattleClauses {
	BATON_PASS_1("baton_pass_1", batonPass1Clause()),
	BATON_PASS("baton_pass", batonPassClause()),
	EVASION_MOVES("evasion_moves", evasionMovesClause()),
	EVASION_ABILITIES("evasion_abilities", evasionAbilitiesClause()),
	ONE_HIT_KNOCKOUT("one_hit_knockout", oneHitKnockOutClause()),
	SPECIES("species", speciesClause()),
	CHATTER("chatter", chatterClause()),
	DRIZZLE("drizzle", drizzleClause()),
	DROUGHT("drought", droughtClause()),
	DRIZZLE_SWIM("drizzle_swim", drizzleSwimClause()),
	ITEM("item", itemClause()),
	LEGENDARY("legendary", legendaryClause()),
	MOODY("moody", moodyClause()),
	SHADOW_TAG("shadow_tag", shadowTagClause()),
	SWAGGER("swagger", swaggerClause()),
	SMASH_PASS("smash_pass", smashPassClause()),
	SAND_STREAM("sand_stream", sandStreamClause()),
	SNOW_WARNING("snow_warning", snowWarningClause());

	private final String id;
	private final GymPreBattleClause clause;

	GymPreBattleClauses(String id, GymPreBattleClause clause) {
		this.id = id;
		this.clause = clause;
	}

	public static @Nullable GymPreBattleClause getByName(String name) {
		for (GymPreBattleClauses clause : GymPreBattleClauses.values()) {
			if (name.equals(clause.getId()) || name.equals(clause.getId() + "_clause")) {
				return clause.getClause();
			}
		}

		return null;
	}

	public static GymPreBattleClause batonPassClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedBatonPassClauseMessage,
				(party) -> !hasAnyMoveInParty(party, "batonpass")
		);
	}

	public static GymPreBattleClause evasionMovesClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedEvasionMovesClauseMessage,
				(party) -> !hasAnyMoveInParty(party, List.of("doubleteam", "minimize"))
		);
	}

	public static GymPreBattleClause evasionAbilitiesClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedEvasionAbilitiesClauseMessage,
				(party) -> !hasAbilitiesInParty(party, List.of("sandveil", "snowcloak"))
		);
	}

	public static GymPreBattleClause oneHitKnockOutClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedOhkoClauseMessage,
				(party) -> !hasAnyMoveInParty(party, List.of(
						"fissure",
						"guillotine",
						"horndrill",
						"sheercold"
				))
		);
	}

	public static GymPreBattleClause speciesClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedSpeciesClauseMessage,
				(party) -> {
					List<String> species = new ArrayList<>(party.getSize());
					for (int i = 0; i < party.getSize(); i++) {
						if (party.getPokemon(i) == null) {
							continue;
						}

						String speciesName = party.getPokemon(i).getSpeciesName();
						if (species.contains(speciesName)) {
							return false;
						}
						species.add(speciesName);
					}

					return true;
				}
		);
	}

	/// //////////////////////////////////////// From AI details ////////////////////////////////////

	public static GymPreBattleClause chatterClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedChatterClauseMessage,
				(party) -> !hasAnyMoveInParty(party, "chatter"));
	}

	public static GymPreBattleClause drizzleClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedDrizzleClauseMessage,
				(party) -> !hasAbilityInParty(party, "drizzle")
		);
	}

	public static GymPreBattleClause droughtClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedDroughtClauseMessage,
				(party) -> !hasAbilityInParty(party, "drought")
		);
	}

	public static GymPreBattleClause drizzleSwimClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedDrizzleSwimClauseMessage,
				(party) -> !hasAllAbilitiesInParty(party, List.of("drizzle", "swiftswim"))
		);
	}

	public static GymPreBattleClause itemClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedItemClauseMessage,
				(party) -> {
					List<String> heldItems = new ArrayList<>(party.getSize());
					for (int i = 0; i < party.getSize(); i++) {
						if (party.getPokemon(i) == null) {
							continue;
						}

						String heldItemString = party.getPokemon(i).getHeldItemString();
						if (heldItems.contains(heldItemString)) {
							return false;
						}
						heldItems.add(heldItemString);
					}

					return true;
				}
		);
	}

	public static GymPreBattleClause legendaryClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedLegendaryClauseMessage,
				(party) -> {
					List<String> mythicalList = PokemonBackendModule.instance().getConfig().pokemonClasses.get(PokemonClass.MYTHICAL);
					List<String> legendaryList = PokemonBackendModule.instance().getConfig().pokemonClasses.get(PokemonClass.LEGENDARY);
					for (int i = 0; i < party.getSize(); i++) {
						if (party.getPokemon(i) == null) {
							continue;
						}

						String species = party.getPokemon(i).getSpeciesName();
						if (mythicalList.contains(species) || legendaryList.contains(species)) {
							return false;
						}
					}

					return true;
				}
		);
	}

	public static GymPreBattleClause moodyClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedMoodyClauseMessage,
				(party) -> !hasAbilityInParty(party, "moody")
		);
	}

	public static GymPreBattleClause shadowTagClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedShadowTagClauseMessage,
				(party) -> !hasAbilityInParty(party, "shadowtag")
		);
	}

	public static GymPreBattleClause swaggerClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedSwaggerClauseMessage,
				(party) -> !hasAbilityInParty(party, "swagger")
		);
	}

	public static GymPreBattleClause smashPassClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedSmashPassClauseMessage,
				(party) -> !hasAllMovesOnOnePokemon(party, List.of("batonpass", "shellsmash"))
		);
	}

	public static GymPreBattleClause sandStreamClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedSandStreamClauseMessage,
				(party) -> !hasAbilityInParty(party, "sandstream")
		);
	}

	public static GymPreBattleClause snowWarningClause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedSnowWarningMessage,
				(party) -> !hasAbilityInParty(party, "snowwarning")
		);
	}

	public static GymPreBattleClause batonPass1Clause() {
		return new GymPreBattleClause(
				GymsBackendModule.instance().getConfig().lang.failedBatonPass1ClauseMessage,
				(party) -> !hasMoveAtMostOnce(party, "batonpass")
		);
	}

	private static boolean hasAllMovesOnOnePokemon(IPokemonParty party, List<String> movesToFind) {
		for (int i = 0; i < party.getSize(); i++) {
			if (party.getPokemon(i) == null) {
				continue;
			}
			List<String> currentMovesToFind = new ArrayList<>(movesToFind);

			for (String move : party.getPokemon(i).getMovesIDs()) {
				currentMovesToFind.remove(move);
			}

			if (currentMovesToFind.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private static boolean hasMoveAtMostOnce(IPokemonParty party, String moveToFind) {
		boolean foundBefore = false;
		for (int i = 0; i < party.getSize(); i++) {
			if (party.getPokemon(i) == null) {
				continue;
			}

			for (String move : party.getPokemon(i).getMovesIDs()) {
				if (moveToFind.equals(move)) {
					if (foundBefore) {
						return true;
					}
					foundBefore = true;
				}
			}
		}

		return false;
	}

	private static boolean hasAnyMoveInParty(IPokemonParty party, String moveToFind) {
		return hasAnyMoveInParty(party, List.of(moveToFind));
	}

	private static boolean hasAnyMoveInParty(IPokemonParty party, List<String> movesToFind) {
		for (int i = 0; i < party.getSize(); i++) {
			if (party.getPokemon(i) == null) {
				continue;
			}
			for (String move : party.getPokemon(i).getMovesIDs()) {
				if (movesToFind.contains(move)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean hasAllAbilitiesInParty(IPokemonParty party, List<String> abilitiesToFind) {
		for (int i = 0; i < party.getSize(); i++) {
			if (party.getPokemon(i) == null) {
				continue;
			}

			abilitiesToFind.remove(party.getPokemon(i).getAbilityID());
			if (abilitiesToFind.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private static boolean hasAbilityInParty(IPokemonParty party, String abilityToFind) {
		return hasAbilitiesInParty(party, List.of(abilityToFind));
	}

	private static boolean hasAbilitiesInParty(IPokemonParty party, List<String> abilitiesToFind) {
		for (int i = 0; i < party.getSize(); i++) {
			if (party.getPokemon(i) == null) {
				continue;
			}

			int finalI = i;
			if (abilitiesToFind.stream().anyMatch((ability) -> party.getPokemon(finalI).getMovesIDs().contains(ability))) {
				return true;
			}
		}

		return false;
	}

	/*
	 Already implemented in cobblemon config:
	 - endless
	 - sleep

	 Listed every possible battle rule in the example npc config, as standard they use for the battles
	  obtainable,past,unobtainable the others were added by me. Feel free to remove them as you wish

	TODO:
	 - inverse
	 - mega clause
	 - sky battle

	Need more details:
	 - forfeit
	 - NU / OU / PU / RU / UU / unrestricted
	 - weather speed
	 - speed-pass
	 */
}
