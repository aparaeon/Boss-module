package gg.mmorealms.module.chat_games.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.chat_games.backend.common.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.backend.common.manager.PokemonQuestionGenerator;
import gg.mmorealms.module.chat_games.common.ChatGamesCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class ChatGamesBackendModule extends ChatGamesCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static ChatGamesBackendModule instance;

	private @Inject FileManager fileManager;

	public ChatGamesBackendModule() {
		instance = this;
	}

	protected ChatGamesConfig config;
	protected PokemonQuestionGenerator questionGenerator;

	protected PokemonQuestionGenerator createQuestionGenerator(ChatGamesConfig config) {
		return null;
	}

	@Override
	public void onInit() {
		this.config = fileManager.load(ChatGamesConfig.class);
		this.questionGenerator = createQuestionGenerator(this.config);
		if (this.questionGenerator != null) {
			export(this.questionGenerator, PokemonQuestionGenerator.class);
		}
	}

	@Override
	public void onEnable() {

	}

}
