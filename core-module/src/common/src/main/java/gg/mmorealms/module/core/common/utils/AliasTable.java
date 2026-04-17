package gg.mmorealms.module.core.common.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class AliasTable<V> {
	private final List<V> values = new ArrayList<>();
	private final List<Double> weights = new ArrayList<>();
	private int[] alias;
	private double[] probability;
	private double totalWeight = 0;
	private boolean built = false;

	public void add(V v, double weight) {
		if (built) throw new IllegalStateException("Cannot add to already built table");
		values.add(v);
		weights.add(Math.max(weight, 0)); // Avoid negative weights
		totalWeight += Math.max(weight, 0);
	}

	public void build() {
		int n = values.size();
		if (n == 0) return;

		// Initialize arrays
		probability = new double[n];
		alias = new int[n];

		// Calculate scaled probabilities (multiply by n to get average = 1.0)
		double[] scaledProbability = new double[n];

		List<Integer> small = new ArrayList<>();
		List<Integer> large = new ArrayList<>();

		// Scale the probabilities and sort into small and large
		for (int i = 0; i < n; i++) {
			scaledProbability[i] = (weights.get(i) * n) / totalWeight;
			if (scaledProbability[i] < 1.0) {
				small.add(i);
			} else {
				large.add(i);
			}
		}

		// Build the alias table
		while (!small.isEmpty() && !large.isEmpty()) {
			int smallIdx = small.removeLast();
			int largeIdx = large.removeLast();

			probability[smallIdx] = scaledProbability[smallIdx];
			alias[smallIdx] = largeIdx;

			// Adjust weight of large element and move it to appropriate list
			scaledProbability[largeIdx] = (scaledProbability[largeIdx] + scaledProbability[smallIdx]) - 1.0;
			if (scaledProbability[largeIdx] < 1.0) {
				small.add(largeIdx);
			} else {
				large.add(largeIdx);
			}
		}

		// Handle remaining elements
		while (!small.isEmpty()) {
			probability[small.removeLast()] = 1.0;
		}

		while (!large.isEmpty()) {
			probability[large.removeLast()] = 1.0;
		}

		built = true;
	}

	public V next() {
		if (!built) throw new IllegalStateException("Must build table before sampling");
		int n = values.size();
		if (n == 0) return null;
		if (n == 1) return values.get(0);

		int i = ThreadLocalRandom.current().nextInt(n);
		return ThreadLocalRandom.current().nextDouble() < probability[i]
				? values.get(i)
				: values.get(alias[i]);
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public int size() {
		return values.size();
	}
}