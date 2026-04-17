package gg.mmorealms.module.legendaries.backend.fabric.dto.enums;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;

@Getter
public enum Weather {
	CLEAR(0),
	RAIN(1),
	STORM(2),
	ANY(-1);

	private final int code;

	Weather(int code) {
		this.code = code;
	}

	public static Weather fromWorldState(ServerLevel world) {
		if (world.isThundering()) return STORM;
		if (world.isRaining()) return RAIN;
		return CLEAR;
	}

	public static Weather fromString(String value) {
		if (value == null) return ANY;
		try {
			return valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ANY;
		}
	}

	public boolean matches(Weather currentWeather) {
		return this == ANY || this == currentWeather;
	}
}
