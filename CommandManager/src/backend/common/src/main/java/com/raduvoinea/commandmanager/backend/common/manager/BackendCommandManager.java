package com.raduvoinea.commandmanager.backend.common.manager;

import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.commandmanager.common.config.CommandManagerConfig;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.manager.CommonMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.Injector;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.reflections.Reflections;
import lombok.Getter;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class BackendCommandManager extends CommonCommandManager {

	private final MinecraftServer server;
	private final BackendMiniMessageManager miniMessageManager;

	public BackendCommandManager(@NotNull Reflections.Crawler reflectionsCrawler,
	                             @NotNull CommandManagerConfig config, @NotNull MinecraftServer server,
	                             @NotNull Holder<Injector> injector,
	                             @NotNull BackendMiniMessageManager miniMessageManager) {
		super(reflectionsCrawler, ServerPlayer.class, MinecraftServer.class, CommandSource.class, config, injector);

		this.server = server;
		this.miniMessageManager = miniMessageManager;
	}

	@Override
	protected void platformRegister(@NotNull CommonCommand primitiveCommand) {
		BackendCommand command = (BackendCommand) primitiveCommand;

		for (String alias : command.getAliases()) {
			server.getCommands().getDispatcher().register(command.getCommandBuilder(alias));
		}
	}

	@Override
	public void sendMessage(Object target, String message) {
		if (target instanceof ServerPlayer player) {
			player.sendSystemMessage(miniMessageManager.parse(message));
			return;
		}

		if (target instanceof MinecraftServer console) {
			console.sendSystemMessage(miniMessageManager.parse(message));
			return;
		}

		if (target instanceof CommandSource output) {
			output.sendSystemMessage(miniMessageManager.parse(message));
			return;
		}

		Logger.error("Unknown target type: " + target.getClass().getName());
	}

}
