package com.raduvoinea.utils.logger.dto;


import lombok.Getter;

@Getter
public enum Level {

	ERROR(40),
	WARN(30),
	INFO(20),
	DEBUG(10),
	TRACE(0);

	private final int level;

	Level(int level) {
		this.level = level;
	}

	@SuppressWarnings("unused")
	public static Level fromInt(int levelInt) {
		for (Level level : Level.values()) {
			if (level.level == levelInt) {
				return level;
			}
		}
		return null;
	}

}
