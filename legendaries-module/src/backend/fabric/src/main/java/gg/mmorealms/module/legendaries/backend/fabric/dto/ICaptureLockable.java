package gg.mmorealms.module.legendaries.backend.fabric.dto;

import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface ICaptureLockable {
	@Nullable Set<UUID> core$getAllowedCatchers();

	void core$setAllowedCatchers(@Nullable Set<UUID> set);
}
