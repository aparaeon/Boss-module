package com.raduvoinea.commandmanager.velocity.manager;

import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.commandmanager.common.config.CommandManagerConfig;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.Injector;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.reflections.Reflections;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class VelocityCommandManager extends CommonCommandManager {

	private final ProxyServer proxy;
	private final VelocityMiniMessageManager miniMessageManager;
	private final Object plugin;

	public VelocityCommandManager(@NotNull Object plugin, @NotNull ProxyServer proxy,
	                              @NotNull Reflections.Crawler reflectionsCrawler, @NotNull CommandManagerConfig config,
	                              @NotNull Holder<Injector> injectorHolder) {
		super(reflectionsCrawler, Player.class, ConsoleCommandSource.class, CommandSource.class, config, injectorHolder);

		this.plugin = plugin;
		this.proxy = proxy;
		this.miniMessageManager = new VelocityMiniMessageManager();
	}

	@Override
	protected void platformRegister(@NotNull CommonCommand primitiveCommand) {
		VelocityCommand command = (VelocityCommand) primitiveCommand;

		com.velocitypowered.api.command.CommandManager commandManager = proxy.getCommandManager();

		CommandMeta commandMeta = commandManager.metaBuilder(command.getMainAlias())
				.aliases(command.getAliases().subList(1, command.getAliases().size()).toArray(new String[0]))
				.plugin(this.plugin)
				.build();


		commandManager.register(commandMeta, command);
	}

	@Override
	public final void sendMessage(Object user, String message) {
		CommandSource source = (CommandSource) user;
		source.sendMessage(miniMessageManager.parse(message));
	}

}
