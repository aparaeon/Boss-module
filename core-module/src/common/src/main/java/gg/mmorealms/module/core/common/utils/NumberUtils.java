package gg.mmorealms.module.core.common.utils;

import com.raduvoinea.utils.generic.dto.Pair3;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class NumberUtils {

	// Unit name - threshold - amount to div by
	private static List<Pair3<String, Double, Double>> UNITS = List.of(
			new Pair3<>("ab", 1_000_000_000_000_000_000.0, 1_000_000_000_000_000_000.0),
			new Pair3<>("aa", 1_000_000_000_000_000.0, 1_000_000_000_000_000.0),
			new Pair3<>("T", 1_000_000_000_000.0, 1_000_000_000_000.0),
			new Pair3<>("B", 1_000_000_000.0, 1_000_000_000.0),
			new Pair3<>("M", 1_000_000.0, 1_000_000.0),
			new Pair3<>("K", 10_000.0, 1_000.0)
	);

	public static double getPercentage(double value, double maximum) {
		if (maximum == 0) {
			throw new IllegalArgumentException("Maximum value cannot be zero.");
		}

		double percentage = (value / maximum) * 100;
		return Math.round(percentage * 100.0) / 100.0;
	}

	public static String formatNumberWithCommas(int number) {
		return String.format("%,d", number);
	}

	/**
	 * Formats a number with a unit suffix (e.g. K, M, B) rounded to 1 decimal place,
	 * with the {@code .0} suffix removed when the result is a whole number.
	 *
	 * <p>The unit and its thresholds are sourced from {@link #UNITS}, where each entry
	 * defines the suffix, the threshold to apply it, and the divisor. Numbers below
	 * the smallest unit threshold are formatted with comma grouping and no decimals.
	 *
	 * <p>Examples:
	 * <pre>
	 *   12345.6  → "12.3 K"
	 *   12000.0  → "12 K"
	 *   2350.0   → "2,350"
	 * </pre>
	 *
	 * @param number the number to format
	 * @return a human-readable string with a unit suffix, rounded to 1 decimal place
	 * @see #formatNumberWithUnitsPrecise(double) for higher decimal precision
	 */
	public static String formatNumberWithUnits(double number) {
		for (Pair3<String, Double, Double> unit : UNITS) {
			String string = unit.first();
			double threshold = unit.second();
			if (number >= threshold) {
				double formatted = number / unit.third();
				return String.format("%.1f %s", formatted, string).replace(".0", "");
			}
		}

		return String.format("%,.0f", number);
	}

	/**
	 * Formats a number with a unit suffix (e.g. K, M, B) using up to 4 significant
	 * decimal places, with all trailing zeros removed.
	 *
	 * <p>The unit and its thresholds are sourced from {@link #UNITS}, where each entry
	 * defines the suffix, the threshold to apply it, and the divisor. Numbers below
	 * the smallest unit threshold are formatted with comma grouping and no decimals.
	 *
	 * <p>Examples:
	 * <pre>
	 *   12345.67891012 → "12.3457 K"
	 *   12300.0        → "12.3 K"
	 *   12000.0        → "12 K"
	 *   999.0          → "999"
	 * </pre>
	 *
	 * @param number the number to format
	 * @return a human-readable string with a unit suffix and no trailing zeros
	 */
	public static String formatNumberWithUnitsPrecise(double number) {
		DecimalFormat df = new DecimalFormat("0.####", DecimalFormatSymbols.getInstance());

		for (Pair3<String, Double, Double> unit : UNITS) {
			String string = unit.first();
			double threshold = unit.second();
			if (number >= threshold) {
				double formatted = number / unit.third();
				return df.format(formatted) + " " + string;
			}
		}

		return df.format(number);
	}

	public static String formatNumberWithDecimalPlaces(double number, int decimals) {
		String format = "%,." + decimals + "f";
		return String.format(format, number);
	}

}
