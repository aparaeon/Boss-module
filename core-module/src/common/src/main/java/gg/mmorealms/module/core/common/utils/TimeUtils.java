package gg.mmorealms.module.core.common.utils;

import com.google.common.primitives.Ints;
import com.raduvoinea.utils.generic.Time;

public class TimeUtils {

	private TimeUtils() {
	}

	public static int timeToTick(Time time) {
		return Ints.checkedCast(time.toMilliseconds() / 50);
	}

}