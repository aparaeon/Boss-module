package gg.mmorealms.loader.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils extends com.raduvoinea.utils.file_manager.utils.DateUtils {

	public static String formatTimestamp(long unixTimestamp, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format)
				.withZone(ZoneId.systemDefault());
		return formatter.format(Instant.ofEpochMilli(unixTimestamp));
	}

	public static long getStartOfMonth(int negativeDelta) {
		LocalDate now = LocalDate.now();
		LocalDate start = now.minusMonths(negativeDelta).withDayOfMonth(1);
		return start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

}
