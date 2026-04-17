package gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces;

import com.google.gson.JsonObject;
import gg.mmorealms.module.core.backend.common.utils.ItemBuilder;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.core.common.utils.StringUtils;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface IPokemon {

	String MEGA_KEY = "mega";
	String MEGA_X_KEY = "mega_x";
	String MEGA_Y_KEY = "mega_y";
	String NEUTERED_KEY = "neutered";

	static IPokemon deserialize(JsonObject json) {
		return PokemonBackendModule.instance().getPlatformImplementation().deserializePokemon(json);
	}

    static @Nullable IPokemon getBySpeciesName(String speciesName) {
        return getBySpeciesName(speciesName, false);
    }

    static @Nullable IPokemon getBySpeciesName(String speciesName, boolean shiny) {
        return PokemonBackendModule.instance().getPlatformImplementation().create(speciesName, shiny);
    }

	Object getNative();

	JsonObject serialize();

	String getSpeciesString();

	String getSpeciesName();

	String getName();

	String getName(boolean showTitle);

    void applyNameStyle(String miniMessage);

    void resetNameStyle();

	int getLevel();

	boolean isShiny();

	void setShiny(boolean shiny);

	boolean isMega();

	boolean isNeutered();

	void setNeutered(boolean neutered);

    List<String> getNatures();

	String getNatureString();

	String getBallString();

	String getHeldItemString();

	String getAbilityID();

	String getAbilityName();

    List<String> getPossibleGenders();

	String getGenderString();

	int getIvsHP();

	int getIvsAttack();

	int getIvsDefence();

	int getIvsSpecialAttack();

	int getIvsSpecialDefence();

	int getIvsSpeed();

	default int getIvsTotal() {
		return this.getIvsHP() +
				this.getIvsAttack() +
				this.getIvsDefence() +
				this.getIvsSpecialAttack() +
				this.getIvsSpecialDefence() +
				this.getIvsSpeed();
	}

	default int getIvsAverage() {
		return getIvsTotal() / 6;
	}

	int getEvsHP();

	int getEvsAttack();

	int getEvsDefence();

	int getEvsSpecialAttack();

	int getEvsSpecialDefence();

	int getEvsSpeed();

	default int getEvsTotal() {
		return this.getEvsHP() +
				this.getEvsAttack() +
				this.getEvsDefence() +
				this.getEvsSpecialAttack() +
				this.getEvsSpecialDefence() +
				this.getEvsSpeed();
	}

	default int getEvsAverage() {
		return getEvsTotal() / 6;
	}

	List<String> getMovesNames();

	List<String> getMovesIDs();

	ItemStack toItemStack();

	String getColor();

	void setColor(String color);

	void heal();

	default ItemBuilder toItemBuilder(boolean extendedDetails) {
		ItemBuilder itemBuilder = ItemBuilder.of(this.toItemStack());

		if (extendedDetails) {
			itemBuilder = itemBuilder
					.lore(getExtendedDetails());
		}

		return itemBuilder;
	}

	default List<String> getExtendedDetails() {
		return PokemonBackendModule.instance().getConfig().lang.pokemonInfo
				.parse("pokemon_held_item", getHeldItemString())
				.parse("pokemon_species", this.getSpeciesName())
				.parse("pokemon_name", this.getName())
				.parse("pokemon_neutered", this.isNeutered() ? "(Neutered)" : "")
				.parse("pokemon_color", StringUtils.toTitleCase(this.getColor()))
				.parse("pokemon_level", this.getLevel())
				.parse("pokemon_shiny", this.isShiny() ? "⭐" : "")
				.parse("pokemon_nature", StringUtils.toTitleCase(this.getNatureString()))
				.parse("pokemon_ability", StringUtils.toTitleCase(this.getAbilityID()))
				.parse("pokemon_gender", StringUtils.toTitleCase(this.getGenderString()))
				.parse("pokemon_pokeball", StringUtils.toTitleCase(this.getBallString()))
				.parse("pokemon_ivs", this.getIvsTotal())
				.parse("pokemon_ivs_percent", NumberUtils.getPercentage(this.getIvsTotal(), 186))
				.parse("pokemon_ivs_hp", this.getIvsHP())
				.parse("pokemon_ivs_attack", this.getIvsAttack())
				.parse("pokemon_ivs_defence", this.getIvsDefence())
				.parse("pokemon_ivs_special_attack", this.getIvsSpecialAttack())
				.parse("pokemon_ivs_special_defence", this.getIvsSpecialDefence())
				.parse("pokemon_ivs_speed", this.getIvsSpeed())
				.parse("pokemon_evs", this.getEvsTotal())
				.parse("pokemon_evs_percent", NumberUtils.getPercentage(this.getEvsTotal(), 510))
				.parse("pokemon_evs_hp", this.getEvsHP())
				.parse("pokemon_evs_attack", this.getEvsAttack())
				.parse("pokemon_evs_defence", this.getEvsDefence())
				.parse("pokemon_evs_special_attack", this.getEvsSpecialAttack())
				.parse("pokemon_evs_special_defence", this.getEvsSpecialDefence())
				.parse("pokemon_evs_speed", this.getEvsSpeed())
				.parse("pokemon_move_1", StringUtils.toTitleCase(this.getMovesNames().get(0)))
				.parse("pokemon_move_2", StringUtils.toTitleCase(this.getMovesNames().get(1)))
				.parse("pokemon_move_3", StringUtils.toTitleCase(this.getMovesNames().get(2)))
				.parse("pokemon_move_4", StringUtils.toTitleCase(this.getMovesNames().get(3)))
				.parse();
	}

	float getScale();

	void setScale(float scale);

	Entity createEntity(ServerLevel level);

	Set<String> getAspects();

	// Used mostly for logger messages
	String getBriefDescription();
}
