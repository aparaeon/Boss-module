package gg.mmorealms.module.chat_games.common.dto;

import com.raduvoinea.utils.generic.dto.IWeighted;
import lombok.Getter;

@Getter
public enum QuestionType implements IWeighted {

	UNSCRAMBLE_POKEMON(1.0),
	UNSCRAMBLE_ABILITY(1.0),
	UNSCRAMBLE_MOVE(1.0),
	UNSCRAMBLE_NATURE(1.0),
	DEX_ENTRY(1.0),
	POKEMON_TYPE(1.0),
	TYPE_POKEMON(1.0),
	POKEMON_ABILITY(1.0),
	ABILITY_POKEMON(1.0),
	POKEMON_FORM(1.0),
	EGG_GROUP_POKEMON(1.0),
	CUSTOM(1.0),
	MATH(1.0);

	private final double weight;

	QuestionType(double weight) {
		this.weight = weight;
	}

	public String getKey() {
		return name().toLowerCase();
	}

}