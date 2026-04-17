package gg.mmorealms.module.core.common.utils;

public class StringUtils {

	public static String toTitleCase(String input) {
		if (input == null || input.isEmpty()) return input;

		StringBuilder result = new StringBuilder();
		boolean capitalizeNext = true;

		for (char c : input.toCharArray()) {
			if (Character.isWhitespace(c)) {
				capitalizeNext = true;
				result.append(c);
			} else if (capitalizeNext) {
				result.append(Character.toUpperCase(c));
				capitalizeNext = false;
			} else {
				result.append(Character.toLowerCase(c));
			}
		}

		return result.toString();
	}

}
