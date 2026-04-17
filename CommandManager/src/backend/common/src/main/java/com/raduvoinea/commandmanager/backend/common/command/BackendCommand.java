package com.raduvoinea.commandmanager.backend.common.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.common.utils.ListUtils;
import com.raduvoinea.commandmanager.backend.common.manager.BackendCommandManager;
import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.utils.logger.Logger;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.literal;

@SuppressWarnings("unused")
public abstract class BackendCommand extends CommonCommand {

	private final BackendCommandManager commandManager;

	public BackendCommand(CommonCommandManager commandManager, Command commandAnnotation) {
		super(commandManager, commandAnnotation);
		this.commandManager = (BackendCommandManager) commandManager;
	}

	public BackendCommand(CommonCommandManager commandManager) {
		super(commandManager);
		this.commandManager = (BackendCommandManager) commandManager;
	}

	public @NotNull Set<BackendCommand> getSubCommands() {
		return getPrimitiveSubCommands()
				.stream().map(command -> (BackendCommand) command)
				.collect(Collectors.toSet());
	}

	@SuppressWarnings("SameReturnValue")
	private int internalExecute(@NotNull CommandContext<CommandSourceStack> context) {
		try {
			CommandSourceStack source = context.getSource();

			List<String> arguments = this.getArguments().stream()
					.map(argument -> {
						if (argument.startsWith("?")) {
							argument = argument.substring(1);
						}

						try {
							return context.getArgument(argument, String.class);
						} catch (IllegalArgumentException e) {
							return null;
						}
					})
					.toList();

			if (source.isPlayer()) {
				execute(source.getPlayer(), arguments);
			} else {
				execute(source.getServer(), arguments);
			}

		} catch (Throwable error) {
			Logger.error(error);
		}

		return 0;
	}

	public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(@NotNull String alias) {
		LiteralArgumentBuilder<CommandSourceStack> command = literal(alias)
				.requires(source -> hasPermission(source, getPermission()));

		for (BackendCommand subCommand : getSubCommands()) {
			Logger.log("Registering subcommand(s): " + subCommand.getAliases() + " for " + alias);
			for (String subAlias : subCommand.getAliases()) {
				command = command.then(subCommand.getCommandBuilder(subAlias));
			}
		}

		List<ArgumentBuilder<CommandSourceStack, ?>> arguments = new ArrayList<>();

		for (String argument : getArguments()) {
			ArgumentType<String> argumentType = StringArgumentType.string();

			if (argument.startsWith("?")) {
				argument = argument.substring(1);
			}
			if (argument.endsWith("...")) {
				argumentType = StringArgumentType.greedyString();
			}

			String finalArgument = argument;
			arguments.add(Commands
					.argument(argument, argumentType)
					.suggests((context, builder) -> {
								String argumentValue = "";
								try {
									argumentValue = context.getArgument(finalArgument, String.class);
								} catch (IllegalArgumentException ignored) {
								}

								List<String> suggestions = ListUtils.getListThatStartsWith(onAutoComplete(finalArgument, context), argumentValue);

								for (String suggestion : suggestions) {
									builder.suggest(suggestion);
								}

								return builder.buildFuture();
							}
					));
		}

		ArgumentBuilder<CommandSourceStack, ?> then = null;

		if (!arguments.isEmpty()) {
			arguments.getLast().executes(this::internalExecute);

			if (arguments.size() != 1) {
				for (int index = arguments.size() - 2; index >= 0; index--) {
					arguments.get(index).then(arguments.get(index + 1));

					if (getArguments().get(index + 1).startsWith("?")) {
						arguments.get(index).executes(this::internalExecute);
					}
				}
			}

			then = arguments.getFirst();

			if (getArguments().get(0).startsWith("?")) {
				command.executes(this::internalExecute);
			}
		} else {
			command.executes(this::internalExecute);
		}

		if (then != null) {
			command.then(then);
		}

		return command;
	}

	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return new ArrayList<>();
	}

	@Override
	protected final void internalExecutePlayer(@NotNull Object player, @NotNull List<String> arguments) {
		executePlayer((ServerPlayer) player, arguments);
	}

	@Override
	protected final void internalExecuteConsole(@NotNull Object console, @NotNull List<String> arguments) {
		executeConsole((MinecraftServer) console, arguments);
	}

	@Override
	protected final void internalExecuteCommon(@NotNull Object sender, @NotNull List<String> arguments) {
		executeCommon((CommandSource) sender, arguments);
	}

	protected void executePlayer(@NotNull ServerPlayer player, @NotNull List<String> arguments) {

	}

	protected void executeConsole(@NotNull MinecraftServer console, @NotNull List<String> arguments) {

	}

	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {

	}

	protected List<String> recommendPlayersList() {
		return commandManager.getServer().getPlayerList().getPlayers().stream()
				.map(ServerPlayer::getDisplayName)
				.map(Component::getString)
				.toList();
	}

	protected boolean hasPermission(@NotNull CommandSourceStack source, @NotNull String permission) {
		if (!source.isPlayer()) {
			return true;
		}

		return LuckPermsUtils.checkPermission(ServerPlayer.class, source.getPlayer(), permission);
	}
}
