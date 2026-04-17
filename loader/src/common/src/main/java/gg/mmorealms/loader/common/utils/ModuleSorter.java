package gg.mmorealms.loader.common.utils;


import gg.mmorealms.loader.common.dto.LoadedModule;
import gg.mmorealms.loader.common.dto.ModuleID;
import gg.mmorealms.loader.common.exception.CircularDependencyException;

import java.util.*;

public class ModuleSorter {

	public static List<LoadedModule> sortModulesByDependencies(List<LoadedModule> modules) throws CircularDependencyException {
		Map<String, LoadedModule> idToModule = new HashMap<>();
		for (LoadedModule metadataList : modules) {
			idToModule.put(metadataList.getId().id(), metadataList);
		}
		Map<String, List<String>> adjacencyList = new HashMap<>();
		for (LoadedModule module : modules) {
			adjacencyList.computeIfAbsent(module.getId().id(), ignored -> new ArrayList<>());
		}
		for (LoadedModule module : modules) {
			for (ModuleID dependency : module.getDependencies()) {
				if (!idToModule.containsKey(dependency.id())) {
					continue;
				}
				adjacencyList.get(dependency.id()).add(module.getId().id());
			}
		}
		List<String> sortedIds = topologicalSort(adjacencyList);
		List<LoadedModule> sortedModules = new ArrayList<>();
		for (String id : sortedIds) {
			sortedModules.add(idToModule.get(id));
		}
		return sortedModules;
	}

	public static List<String> topologicalSort(Map<String, List<String>> adjacencyList) throws CircularDependencyException {
		Map<String, Integer> state = new HashMap<>();
		adjacencyList.forEach((node, neighbors) -> {
			state.putIfAbsent(node, 0);
			neighbors.forEach(neighbor -> state.putIfAbsent(neighbor, 0));
		});

		List<String> sorted = new ArrayList<>();
		List<String> path = new ArrayList<>();
		for (String node : adjacencyList.keySet()) {
			if (state.get(node) == 0) {
				dfs(node, adjacencyList, state, sorted, path);
			}
		}

		Collections.reverse(sorted);
		return sorted;
	}


	private static void dfs(String node, Map<String, List<String>> adjacencyList, Map<String, Integer> state, List<String> sorted, List<String> path) throws CircularDependencyException {
		path.add(node);
		state.put(node, 1);

		try {
			for (String neighbor : adjacencyList.getOrDefault(node, Collections.emptyList())) {
				if (state.get(neighbor) == 0) {
					dfs(neighbor, adjacencyList, state, sorted, path);
				} else if (state.get(neighbor) == 1) {
					int index = path.indexOf(neighbor);
					List<String> cycle = new ArrayList<>(path.subList(index, path.size()));
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
