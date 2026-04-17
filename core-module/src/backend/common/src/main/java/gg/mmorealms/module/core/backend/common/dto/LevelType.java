package gg.mmorealms.module.core.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum LevelType {
	OVERWORLD(
			ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse("overworld")),
			List.of("wilds", "overworld")
	),
	NETHER(
			ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse("the_nether")),
			List.of("nether")
	),
	END(
			ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse("the_end")),
			List.of("end")
	);

	private final ResourceKey<Level> resourceKey;
	private final List<String> friendlyNames;

	private static List<String> suggestionList = null;

	public static @NotNull LevelType get(@Nullable String name) {
		if (name == null) {
			return LevelType.OVERWORLD;
		}

		for (LevelType type : LevelType.values()) {
			if (type.getResourceKey().location().getPath().equals(name) || type.getFriendlyNames().contains(name)) {
				return type;
			}
		}

		return LevelType.OVERWORLD;
	}

	public static List<String> getSuggestionList() {
		if (suggestionList != null) {
			return suggestionList;
		}

		suggestionList = new ArrayList<>(LevelType.values().length);

		for (LevelType levelType : LevelType.values()) {
			suggestionList.addAll(levelType.getFriendlyNames());
		}

		return suggestionList;
	}
}
