package com.raduvoinea.commandmanager.fabric.manager;

import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.commandmanager.common.config.CommandManagerConfig;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.fabric.command.FabricCommand;
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
public class FabricCommandManager extends CommonCommandManager {

	private final MinecraftServer server;
	private final FabricMiniMessageManager miniMessageManager;

	public FabricCommandManager(@NotNull Reflections.Crawler reflectionsCrawler, @NotNull CommandManagerConfig config,
	                            @NotNull MinecraftServer server, @NotNull Holder<Injector> injector) {
		super(reflectionsCrawler, ServerPlayer.class, MinecraftServer.class, CommandSource.class, config, injector);

		this.server = server;
		this.miniMessageManager = new FabricMiniMessageManager(server);
	}

	@Override
	protected void platformRegister(@NotNull CommonCommand primitiveCommand) {
		FabricCommand command = (FabricCommand) primitiveCommand;

		for (String alias : command.getAliases()) {
			server.getCommands().getDispatcher().register(command.getCommandBuilder(alias));
		}

		server.getPlayerList().getPlayers().forEach(player ->
				server.getCommands().sendCommands(player)
		);
	}

	@Override
	public void sendMessage(Object target, String message) {
		if (target instanceof ServerPlayer player) {
			player.sendSystemMessage(miniMessageManager.parse(message));
			return;
		}

		if (target instanceof MinecraftServer console) {
			console.sendSystemMessage(Component.literal(message));
			return;
		}

		if (target instanceof CommandSource output) {
			output.sendSystemMessage(Component.literal(message));
			return;
		}

		Logger.error("Unknown target type: " + target.getClass().getName());
	}

}
