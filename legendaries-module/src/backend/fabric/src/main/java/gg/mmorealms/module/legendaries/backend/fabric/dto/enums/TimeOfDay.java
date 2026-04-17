package gg.mmorealms.module.legendaries.backend.fabric.dto.enums;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum TimeOfDay {
	ANY(-1, -1),
	DAWN(23000, 23999), // (05:00-05:59)
	MORNING(0, 3999), // (06:00-09:59)
	NOON(4000, 7999), // (10:00-13:59)
	AFTERNOON(8000, 11999), // (14:00-17:59)
	DAY(0, 11999), // (06:00-17:59)
	DUSK(12000, 12999), // (18:00-18:59)
	NIGHT(13000, 22999), // (19:00-04:59)
	MIDNIGHT(18000, 19999); // (00:00-01:59)

	private final int startTick;
	private final int endTick;

	TimeOfDay(int startTick, int endTick) {
		this.startTick = startTick;
		this.endTick = endTick;
	}

	public static TimeOfDay fromString(@Nullable String value) {
		if (value == null) return ANY;
		try {
			return valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ANY;
		}
	}

	/**
	 * Compares Minecraft world time to TimeOfDay enum
	 */
	public boolean matches(long worldTime) {
		if (this == ANY) return true;
		long dayTick = Math.floorMod(worldTime, 24_000L); // bound value to minecraft day range

		// Handle cases that wrap around midnight
		if (this.startTick > this.endTick) {
			return dayTick >= this.startTick || dayTick <= this.endTick;
		} else {
			return dayTick >= this.startTick && dayTick <= this.endTick;
		}
	}

	/**
	 * Gets all matching time periods for a given world time
	 */
	public static List<TimeOfDay> getAllMatchingTimes(long worldTime, boolean excludeAny) {
		return Arrays.stream(values())
				.filter(time -> {
					if (time.equals(ANY) && excludeAny) return false;
					return time.matches(worldTime);
				})
				.collect(Collectors.toList());
	}
}
