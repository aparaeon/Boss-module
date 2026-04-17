package gg.mmorealms.loader.common.utils;

public class StringUtils {

	public static String toPascalCase(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		StringBuilder result = new StringBuilder();
		boolean capitalizeNext = true;

		for (char c : input.toCharArray()) {
			if (Character.isLetterOrDigit(c)) {
				if (capitalizeNext) {
					result.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					result.append(Character.toLowerCase(c));
				}
			} else {
				capitalizeNext = true;
			}
		}

		return result.toString();
	}

	public static String toSnakeCase(String input) {
		if (input != null && !input.isEmpty()) {
			StringBuilder result = new StringBuilder();
			boolean lastWasSeparator = true;

			for (char c : input.toCharArray()) {
				if (Character.isWhitespace(c)) {
					if (!lastWasSeparator && !result.isEmpty()) {
						result.append('_');
						lastWasSeparator = true;
					}
				} else {
					result.append(Character.toLowerCase(c));
					lastWasSeparator = false;
				}
			}

			int length = result.length();
			if (length > 0 && result.charAt(length - 1) == '_') {
				result.deleteCharAt(length - 1);
			}

			return result.toString();
		} else {
			return input;
		}
	}

	public static String generateRandomCode(int length, String dictionary) {
		StringBuilder code = new StringBuilder(length);
		int dictionaryLength = dictionary.length();

		for (int i = 0; i < length; i++) {
			int index = (int) (Math.random() * dictionaryLength);
			code.append(dictionary.charAt(index));
		}

		return code.toString();
	}

}
