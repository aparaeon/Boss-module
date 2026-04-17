package gg.mmorealms.module.tms.backend.fabric.config;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.raduvoinea.utils.logger.Logger;

/**
 * We use this in TMsConfig,
 * as Cobblemon's ElementalType is class with many fields.
 * Whereas we only need to (de)serialize name of type (e.g., from enum)
 */
public enum TMElementalType {
	NORMAL("Normal"),
	FIRE("Fire"),
	WATER("Water"),
	GRASS("Grass"),
	ELECTRIC("Electric"),
	ICE("Ice"),
	FIGHTING("Fighting"),
	POISON("Poison"),
	GROUND("Ground"),
	FLYING("Flying"),
	PSYCHIC("Psychic"),
	BUG("Bug"),
	ROCK("Rock"),
	GHOST("Ghost"),
	DRAGON("Dragon"),
	DARK("Dark"),
	STEEL("Steel"),
	FAIRY("Fairy");

	private final String friendlyName;

	TMElementalType(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public static TMElementalType fromElementalType(ElementalType elementalType) {
		String name = elementalType.getName().toUpperCase();
		try {
			return valueOf(name);
		} catch (IllegalArgumentException ex) {
			Logger.error("No TMElementalType found by valueOf(" + name + ")");
			return NORMAL;
		}
	}
}
