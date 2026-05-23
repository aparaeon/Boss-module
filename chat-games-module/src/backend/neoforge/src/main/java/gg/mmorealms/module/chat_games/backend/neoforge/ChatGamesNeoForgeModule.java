package gg.mmorealms.module.chat_games.backend.neoforge;

import gg.mmorealms.module.chat_games.ChatGamesModuleBuildConstants;
import gg.mmorealms.module.chat_games.backend.common.ChatGamesBackendModule;
import gg.mmorealms.module.chat_games.backend.common.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.backend.common.manager.PokemonQuestionGenerator;
import gg.mmorealms.module.chat_games.backend.neoforge.manager.PixelmonQuestionGenerator;
import net.neoforged.fml.common.Mod;

@Mod(ChatGamesModuleBuildConstants.ID)
public class ChatGamesNeoForgeModule extends ChatGamesBackendModule {

	public ChatGamesNeoForgeModule() {
		this.setup();
	}

	@Override
	protected PokemonQuestionGenerator createQuestionGenerator(ChatGamesConfig config) {
		return new PixelmonQuestionGenerator(config);
	}

}
