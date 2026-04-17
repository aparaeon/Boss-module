package gg.mmorealms.module.pokemon.backend.fabric.manager;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.BattleType;

public class CobblemonUtils {

	public static boolean isSameBattleType(BattleType battleType, BattleType desiredBattleType) {
		return battleType.getName().equals(BattleFormat.Companion.getGEN_9_SINGLES().getBattleType().getName());
	}

	public static boolean isSameBattleType(BattleFormat battleFormat, BattleType battleType) {
		return isSameBattleType(battleFormat.getBattleType(), battleType);
	}

	public static boolean isSameBattleType(PokemonBattle battle, BattleType battleType) {
		return isSameBattleType(battle.getFormat(), battleType);
	}

	public static boolean isSameBattleType(BattleFormat battleFormat, BattleFormat desiredBattleType) {
		return isSameBattleType(battleFormat.getBattleType(), desiredBattleType.getBattleType());
	}

	public static boolean isSameBattleType(PokemonBattle battle, BattleFormat battleFormat) {
		return isSameBattleType(battle.getFormat(), battleFormat.getBattleType());
	}
}
