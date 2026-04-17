package gg.mmorealms.module.gambling.backend.common.dto;

import lombok.Getter;

@Getter
public enum RouletteSlotType {
	RED("<red>Red"),
	BLACK("<gray>Black"),
	GREEN("<green>Green"),
	UNKNOWN("<yellow>N/A");

	private final String friendlyName;

	RouletteSlotType(String friendlyName){
		this.friendlyName = friendlyName;
	}
}