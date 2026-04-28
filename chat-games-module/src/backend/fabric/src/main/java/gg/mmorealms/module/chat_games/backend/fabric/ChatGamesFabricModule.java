package gg.mmorealms.module.chat_games.backend.fabric;

import gg.mmorealms.module.chat_games.backend.common.ChatGamesBackendModule;
import gg.mmorealms.module.chat_games.backend.common.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.backend.common.manager.PokemonQuestionGenerator;
import gg.mmorealms.module.chat_games.backend.fabric.manager.CobblemonQuestionGenerator;
import net.fabricmc.api.ModInitializer;

public class ChatGamesFabricModule extends ChatGamesBackendModule implements ModInitializer {

	@Override
	protected PokemonQuestionGenerator createQuestionGenerator(ChatGamesConfig config) {
		return new CobblemonQuestionGenerator();
	}

	@Override
	public void onInit() {
		super.onInit();
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}