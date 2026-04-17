package com.raduvoinea.utils.generic;

import com.raduvoinea.utils.generic.dto.ITopologicalSortable;
import com.raduvoinea.utils.generic.exception.CircularDependencyException;

import java.util.*;

public class TopologicalSorter {

	public static <ID, Element extends ITopologicalSortable<ID>> List<Element> topologicalSort(List<Element> items) throws CircularDependencyException {
		Map<ID, Element> idToModule = new HashMap<>();
		for (Element item : items) {
			idToModule.put(item.getID(), item);
		}

		Map<ID, List<ID>> adjacencyList = new HashMap<>();
		for (Element module : items) {
			adjacencyList.computeIfAbsent(module.getID(), ignored -> new ArrayList<>());
		}

		for (Element module : items) {
			for (ID dependencyID : module.getDependencies()) {
				if (!idToModule.containsKey(dependencyID)) {
					continue;
				}
				adjacencyList.get(dependencyID).add(module.getID());
			}
		}

		List<ID> sortedIDs = internalTopologicalSort(adjacencyList);
		List<Element> sortedModules = new ArrayList<>();

		for (ID id : sortedIDs) {
			sortedModules.add(idToModule.get(id));
		}

		return sortedModules;
	}

	private static <ID> List<ID> internalTopologicalSort(Map<ID, List<ID>> adjacencyList) throws CircularDependencyException {
		Map<ID, Integer> state = new HashMap<>();
		adjacencyList.forEach((node, neighbors) -> {
			state.putIfAbsent(node, 0);
			neighbors.forEach(neighbor -> state.putIfAbsent(neighbor, 0));
		});

		List<ID> sorted = new ArrayList<>();
		List<ID> path = new ArrayList<>();
		for (ID node : adjacencyList.keySet()) {
			if (state.get(node) == 0) {
				dfs(node, adjacencyList, state, sorted, path);
			}
		}

		Collections.reverse(sorted);
		return sorted;
	}

	private static <ID> void dfs(ID node, Map<ID, List<ID>> adjacencyList, Map<ID, Integer> state, List<ID> sorted, List<ID> path) throws CircularDependencyException {
		path.add(node);
		state.put(node, 1);

		try {
			for (ID neighbor : adjacencyList.getOrDefault(node, Collections.emptyList())) {
				if (state.get(neighbor) == 0) {
					dfs(neighbor, adjacencyList, state, sorted, path);
				} else if (state.get(neighbor) == 1) {
					int index = path.indexOf(neighbor);
					List<ID> cycle = new ArrayList<>(path.subList(index, path.size()));
					cycle.add(neighbor);
					throw new CircularDependencyException(cycle);
				}
			}
			state.put(node, 2);
			sorted.add(node);
		} finally {
			path.removeLast();
		}
	}

}