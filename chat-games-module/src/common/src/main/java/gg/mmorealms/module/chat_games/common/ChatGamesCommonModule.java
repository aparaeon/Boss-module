package gg.mmorealms.module.chat_games.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.chat_games.ChatGamesModuleBuildConstants;

@Module(
	id = ChatGamesModuleBuildConstants.ID,
	version = ChatGamesModuleBuildConstants.VERSION,
	authors = {"Kaioshiyazaki"},
	dependencies = ChatGamesModuleBuildConstants.DEPENDENCIES
)
public abstract class ChatGamesCommonModule implements CommonModule {
}
