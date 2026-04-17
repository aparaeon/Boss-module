package gg.mmorealms.loader.common.dto;

import org.jetbrains.annotations.NotNull;

public record ModuleID(String id) implements Comparable<ModuleID> {

	public ModuleID(String id) {
		this.id = id.replace("-", "_");
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || getClass() != other.getClass()) return false;

		ModuleID moduleID = (ModuleID) other;
		return id.equals(moduleID.id);
	}

	@Override
	public @NotNull String toString() {
		return this.id;
	}

	@Override
	public int compareTo(@NotNull ModuleID other) {
		return this.id.compareTo(other.id);
	}
}
