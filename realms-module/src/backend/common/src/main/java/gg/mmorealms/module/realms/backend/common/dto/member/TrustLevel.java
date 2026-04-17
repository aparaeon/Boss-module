package gg.mmorealms.module.realms.backend.common.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum TrustLevel {
	UNKNOWN("Unknown", "", 0),
	VISITOR("Visitor", "<grey>", 10),
	MEMBER("Member", "<aqua>", 20),
	OFFICER("Officer", "<yellow>", 30),
	MANAGER("Manager", "<gold>", 40),
	OWNER("Owner", "<dark_red>", 50);

	private final String name;
	private final String displayColor;
	private final int level;

	TrustLevel(String name, String displayColor, int level) {
		this.name = name;
		this.displayColor = displayColor;
		this.level = level;
	}

	public static TrustLevel fromLevel(int level) {
		return Arrays.stream(values())
				.filter(t -> t.level == level)
				.findFirst()
				.orElse(UNKNOWN);
	}

	public String getDisplayName() {
		return displayColor + name;
	}

	public static @NotNull TrustLevel fromName(String name) {
		if (name == null) return UNKNOWN;
		String normalized = name.trim().toLowerCase();
		return Arrays.stream(values())
				.filter(t -> t.name().equalsIgnoreCase(name) || t.name.equalsIgnoreCase(normalized))
				.findFirst()
				.orElse(UNKNOWN);
	}

	public static @NotNull List<String> getAcceptedLevels() {
		return Arrays.stream(values())
				.filter(t -> t != UNKNOWN && t != OWNER)
				.map(TrustLevel::getName)
				.collect(Collectors.toList());
	}
}
