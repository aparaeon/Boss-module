package com.raduvoinea.commandmanager.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.common.utils.ListUtils;
import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.commandmanager.velocity.manager.VelocityCommandManager;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

// TODO(low) Maybe Add minimum number of arguments and check for it
// TODO(low) Send help command instead of the the "This command does not implement the logic for <> execution"
public abstract class VelocityCommand extends CommonCommand implements SimpleCommand {

	private final VelocityCommandManager commandManager;

	public VelocityCommand(CommonCommandManager commandManager, Command commandAnnotation) {
		super(commandManager, commandAnnotation);
		this.commandManager = (VelocityCommandManager) commandManager;
	}

	public VelocityCommand(CommonCommandManager commandManager) {
		super(commandManager);
		this.commandManager = (VelocityCommandManager) commandManager;
	}

	public @NotNull List<String> onAutoCompleteFiltered(List<String> arguments, CommandSource source) {
		List<String> allArguments = new ArrayList<>();

		for (VelocityCommand subCommand : getSubCommands()) {
			if (hasPermission(source, subCommand.getPermission())) {
				allArguments.addAll(subCommand.getAliases());
			}
		}

		return allArguments;
	}


	@Override
	public final List<String> suggest(Invocation invocation) {
		CommandSource source = invocation.source();
		int argsLength = invocation.arguments().length;
		List<String> arguments = Arrays.asList(invocation.arguments());

		if (!hasPermission(source, getPermission())) {
			return Collections.emptyList();
		}

		if (argsLength == 0) {
			return onAutoCompleteFiltered(arguments, source);
		}

		String lastArg = invocation.arguments()[argsLength - 1];
		VelocityCommand subCommand = getSubCommand(lastArg);

		// Try second-to-last arg in case it's two-depth
		if (subCommand == null && argsLength > 1) {
			String lastArg2 = invocation.arguments()[argsLength - 2];
			subCommand = getSubCommand(lastArg2);
			if (subCommand != null && subCommand.hasPermission(source, subCommand.getPermission())) {
				return ListUtils.getListThatStartsWith(subCommand.onAutoComplete(arguments), lastArg);
			}
		} else if (subCommand != null && subCommand.hasPermission(source, subCommand.getPermission())) {
			return ListUtils.getListThatStartsWith(subCommand.onAutoComplete(arguments), lastArg);
		}

		return ListUtils.getListThatStartsWith(onAutoComplete(arguments), lastArg);
	}


	private @Nullable VelocityCommand getSubCommand(String name) {
		for (VelocityCommand subCommand : getSubCommands()) {
			for (String commandName : subCommand.getAliases()) {
				if (name.equalsIgnoreCase(commandName)) {
					return subCommand;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unused")
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return defaultAutoComplete();
	}

	public @NotNull List<String> defaultAutoComplete() {
		List<String> allArguments = new ArrayList<>();

		for (VelocityCommand subCommand : getSubCommands()) {
			allArguments.addAll(subCommand.getAliases());
		}

		return allArguments;
	}

	@Override
	public final void execute(Invocation invocation) {
		execute(invocation.source(), Arrays.asList(invocation.arguments()));
	}

	public @NotNull Set<VelocityCommand> getSubCommands() {
		return getPrimitiveSubCommands()
				.stream().map(command -> (VelocityCommand) command)
				.collect(Collectors.toSet());
	}

	@Override
	protected final void internalExecutePlayer(@NotNull Object player, @NotNull List<String> arguments) {
		executePlayer((Player) player, arguments);
	}

	@Override
	protected final void internalExecuteConsole(@NotNull Object console, @NotNull List<String> arguments) {
		executeConsole((ConsoleCommandSource) console, arguments);
	}

	@Override
	protected final void internalExecuteCommon(@NotNull Object sender, @NotNull List<String> arguments) {
		executeCommon((CommandSource) sender, arguments);
	}

	protected void executePlayer(Player player, List<String> arguments) {
		player.sendMessage(Component.text("This command does not implement the logic for player-only execution."));
	}

	@SuppressWarnings({"unused"})
	protected void executeConsole(ConsoleCommandSource console, List<String> arguments) {
		console.sendMessage(Component.text("This command does not implement the logic for console-only execution."));
	}

	protected void executeCommon(CommandSource sender, List<String> arguments) {
		if (sender instanceof Player) {
			executePlayer((Player) sender, arguments);
			return;
		}

		if (sender instanceof ConsoleCommandSource) {
			executeConsole((ConsoleCommandSource) sender, arguments);
			return;
		}

		Logger.error("Unknown command sender type: " + sender.getClass().getName() + " for command: " + getAliases().get(0));
	}

	protected List<String> recommendPlayersList() {
		return commandManager.getProxy().getAllPlayers().stream()
				.map(Player::getUsername)
				.toList();
	}

	protected @Nullable Player getPlayer(String usernameOrUUID) {
		try {
			UUID uuid = UUID.fromString(usernameOrUUID);
			return commandManager.getProxy().getPlayer(uuid).orElse(null);
		} catch (Exception e) {
			return commandManager.getProxy().getPlayer(usernameOrUUID).orElse(null);
		}
	}

	protected boolean hasPermission(@NotNull CommandSource source, @NotNull String permission) {
		if (!(source instanceof Player)) {
			return true;
		}

		return LuckPermsUtils.checkPermission(Player.class, source, permission);
	}
}