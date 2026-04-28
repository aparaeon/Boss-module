package gg.mmorealms.module.chat_games.backend.common.manager;

import gg.mmorealms.module.chat_games.common.dto.GeneratedQuestion;
import gg.mmorealms.module.chat_games.common.dto.QuestionType;
import org.jetbrains.annotations.Nullable;

public interface PokemonQuestionGenerator {

	QuestionType pickWeightedRandom(@Nullable String lastKey);

	@Nullable
	GeneratedQuestion generate(QuestionType type);

}
