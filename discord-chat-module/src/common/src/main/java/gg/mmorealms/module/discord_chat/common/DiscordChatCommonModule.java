package gg.mmorealms.module.discord_chat.common;


import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.discord_chat.DiscordChatModuleBuildConstants;
import lombok.Getter;

@Getter
@Module(
		id = DiscordChatModuleBuildConstants.ID,
		version = DiscordChatModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = DiscordChatModuleBuildConstants.DEPENDENCIES
)
public abstract class DiscordChatCommonModule implements CommonModule {


}
