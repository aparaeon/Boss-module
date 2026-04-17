package com.raduvoinea.utils.generic;

import com.raduvoinea.utils.generic.dto.IWeighted;
import com.raduvoinea.utils.generic.dto.Range;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

	@SuppressWarnings("unused")
	public static int getRandom(Range range) {
		return getRandom(range.getMin(), range.getMax());
	}

	public static int getRandom(int min, int max) {
		if (min == max) {
			return min;
		}

		if (min > max) {
			int tmp = min;
			min = max;
			max = tmp;
		}

		return ThreadLocalRandom.current().nextInt(min, max);
	}

	public static double getRandom(double min, double max) {
		if (min == max) {
			return min;
		}

		if (min > max) {
			double tmp = min;
			min = max;
			max = tmp;
		}

		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	@SuppressWarnings("unused")
	public static @NotNull <T> T getRandom(@NotNull List<T> items) {
		if (items.isEmpty()) {
			throw new IllegalArgumentException("Tried to get random item from empty list");
		}

		if (items.getFirst() instanceof IWeighted) {
			//noinspection unchecked
			return (T) getRandomWeighed(items.stream().map(item -> (IWeighted) item).toList());
		}

		return items.get(getRandom(0, items.size()));
	}

	public static @NotNull <T extends IWeighted> T getRandomWeighed(@NotNull List<T> items) {
		double totalWeight = items.stream().mapToDouble(IWeighted::getWeight).sum();
		double random = getRandom(0, totalWeight);
		for (T item : items) {
			random -= item.getWeight();
			if (random <= 0) {
				return item;
			}
		}
		return items.getLast();
	}

	public static @NotNull <T extends IWeighted> List<T> getNRandomWeighed(@NotNull List<T> items, int n) {
		if (n <= 0) {
			return List.of();
		}

		List<T> output = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			output.add(getRandomWeighed(items));
		}
		return output;
	}

}
