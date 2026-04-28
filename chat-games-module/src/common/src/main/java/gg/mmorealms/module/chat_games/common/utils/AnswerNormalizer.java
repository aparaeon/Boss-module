package gg.mmorealms.module.chat_games.common.utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public final class AnswerNormalizer {

	private static final char APOSTROPHE = '\'';
	private static final char QUOTE = '"';

	private static final Map<Character, Character> CHAR_REPLACEMENTS = new HashMap<>();

	static {
		CHAR_REPLACEMENTS.put('\u2019', APOSTROPHE);
		CHAR_REPLACEMENTS.put('\u2018', APOSTROPHE);
		CHAR_REPLACEMENTS.put('\u201C', QUOTE);
		CHAR_REPLACEMENTS.put('\u201D', QUOTE);

		// Pokemon-specific symbols
		CHAR_REPLACEMENTS.put('\u2640', 'f'); // ♀
		CHAR_REPLACEMENTS.put('\u2642', 'm'); // ♂

		// Explicit accented overrides
		CHAR_REPLACEMENTS.put('\u00E9', 'e');
		CHAR_REPLACEMENTS.put('\u00C9', 'e');
	}

	private AnswerNormalizer() {
	}

	public static String normalize(String input) {
		if (input == null || input.isBlank()) {
			return "";
		}

		String loweredInput = input.trim().toLowerCase();

		String decomposedInput =
			Normalizer.normalize(loweredInput, Normalizer.Form.NFD);

		StringBuilder filteredCharacters =
			new StringBuilder(decomposedInput.length());

		for (int index = 0; index < decomposedInput.length(); index++) {
			char currentCharacter = decomposedInput.charAt(index);

			if (Character.getType(currentCharacter)
				== Character.NON_SPACING_MARK) {
				continue;
			}

			currentCharacter =
				CHAR_REPLACEMENTS.getOrDefault(currentCharacter,
					currentCharacter);

			if (isSeparator(currentCharacter)) {
				filteredCharacters.append(' ');
				continue;
			}

			if (Character.isLetterOrDigit(currentCharacter)) {
				filteredCharacters.append(currentCharacter);
			}
		}

		return collapseConsecutiveSpaces(filteredCharacters);
	}

	private static boolean isSeparator(char character) {
		return character == ' '
			|| character == '-'
			|| character == '_'
			|| character == '/'
			|| character == '.'
			|| character == ','
			|| character == ':';
	}

	private static String collapseConsecutiveSpaces(
		StringBuilder rawInput) {

		StringBuilder collapsedResult =
			new StringBuilder(rawInput.length());

		boolean previousWasSpace = false;

		for (int index = 0; index < rawInput.length(); index++) {
			char currentCharacter = rawInput.charAt(index);

			if (currentCharacter == ' ') {
				if (!previousWasSpace) {
					collapsedResult.append(' ');
					previousWasSpace = true;
				}
			} else {
				collapsedResult.append(currentCharacter);
				previousWasSpace = false;
			}
		}

		int resultLength = collapsedResult.length();
		if (resultLength > 0
			&& collapsedResult.charAt(resultLength - 1) == ' ') {
			collapsedResult.deleteCharAt(resultLength - 1);
		}

		return collapsedResult.toString();
	}
}