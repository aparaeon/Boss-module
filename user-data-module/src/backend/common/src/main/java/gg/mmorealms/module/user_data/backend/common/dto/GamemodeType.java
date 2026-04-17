package gg.mmorealms.module.user_data.backend.common.dto;

import net.minecraft.world.level.GameType;

public enum GamemodeType {

	SURVIVAL(GameType.SURVIVAL),
	CREATIVE(GameType.CREATIVE),
	ADVENTURE(GameType.ADVENTURE),
	SPECTATOR(GameType.SPECTATOR);

	public GameType gameType;

	GamemodeType(GameType gameType) {
		this.gameType = gameType;
	}

	public static GamemodeType fromGameType(GameType type) {
		for (GamemodeType value : values()) {
			if (value.gameType == type) {
				return value;
			}
		}

		return GamemodeType.SURVIVAL;
	}

	public GameType toGameType() {
		return this.gameType;
	}

}
