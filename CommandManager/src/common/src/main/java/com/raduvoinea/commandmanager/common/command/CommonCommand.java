package com.raduvoinea.commandmanager.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.exception.CommandNotAnnotated;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.GenericMessageBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class CommonCommand {

	// Provided
	private CommonCommandManager commandManager;
	private @Getter Command annotation;
	private List<CommonCommand> subCommands;

	public CommonCommand(CommonCommandManager commandManager, Command commandAnnotation) {
		init(commandManager, commandAnnotation);
	}

	public CommonCommand(CommonCommandManager commandManager) throws CommandNotAnnotated {
		init(commandManager, this.getClass().getAnnotation(Command.class));
	}

	private void init(CommonCommandManager commandManager, Command commandAnnotation) {
		this.commandManager = commandManager;
		if (commandAnnotation == null) {
			throw new CommandNotAnnotated("Command " + this.getClass().getName() + " is not annotated with @Command.");
		}

		this.annotation = commandAnnotation;

		subCommands = commandManager.getReflectionsCrawler().getOfType(CommonCommand.class)
				.stream()
				.filter(commandClass -> commandClass.isAnnotationPresent(Command.class))
				.filter(commandClass -> commandClass.getAnnotation(Command.class).parent().equals(this.getClass()))
				.map(commandClass -> {
					try {
						CommonCommand command = commandClass.getConstructor(CommonCommandManager.class).newInstance(commandManager);
						if (!commandManager.getInjectorHolder().isEmpty()) {
							commandManager.getInjectorHolder().value().inject(command);
						}
						return command;
					} catch (Exception error) {
						Logger.error("There was an error while registering sub command " + commandClass.getName() + " for command(s) " + this.getAliases());
						Logger.error(error);
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	public String getFullCommand() {
		String parentFullCommand = "";

		if (!isRootCommand()) {
			CommonCommand parent = commandManager.getCommand(getParent());

			if (parent == null) {
				Logger.error("Command " + getParent().getName() + " is not registered.");
				return "Registration Error";
			}

			parentFullCommand = parent.getFullCommand() + " ";
		}

		return parentFullCommand + this.getMainAlias();
	}

	public String getSimpleUsage() {
		return commandManager.getConfig().simpleUsage
				.parse("command", this.getFullCommand())
				.parse("arguments", String.join(" ",
						getArguments().stream()
								.map(arg -> "<" + arg + ">")
								.toList()
				))
				.parse();
	}

	public List<String> getFullCommandAndSubCommands() {
		List<String> lines = new ArrayList<>();
		lines.add(this.getSimpleUsage());

		for (CommonCommand subCommand : subCommands) {
			lines.addAll(subCommand.getFullCommandAndSubCommands());
		}

		lines.sort(String::compareTo);

		return lines;
	}

	public String getUsage() {
		List<String> subcommands = getFullCommandAndSubCommands();
		subcommands = subcommands.subList(1, subcommands.size());

		if (subcommands.isEmpty()) {
			return getSimpleUsage();
		}

		return commandManager.getConfig().complexUsage
				.parse("simple_usage", getSimpleUsage())
				.parse("sub_commands", String.join("\n", subcommands))
				.parse();
	}

	public void execute(@NotNull Object executor, @NotNull List<String> arguments) {
		if (!commandManager.checkPermission(executor, getPermission())) {
			commandManager.sendMessage(executor, "You don't have permission to execute this command.");
			return;
		}

		if (!arguments.isEmpty()) {
			String subCommandName = arguments.getFirst();
			CommonCommand subCommand = subCommands.stream()
					.filter(command -> command.getAliases().contains(subCommandName))
					.findFirst()
					.orElse(null);

			if (subCommand != null) {
				subCommand.execute(executor, arguments.subList(1, arguments.size()));
				return;
			}
		}

		if (arguments.size() < this.getArguments().size()) {
			sendMessage(executor, getUsage());
			return;
		}

		if (annotation.onlyFor().equals(Command.OnlyFor.PLAYERS)) {
			if (commandManager.getPlayerClass().isInstance(executor)) {
				internalExecutePlayer(commandManager.getPlayerClass().cast(executor), arguments);
				return;
			}
			commandManager.sendMessage(executor, "This command can only be executed by players.");
			return;
		}

		if (annotation.onlyFor().equals(Command.OnlyFor.CONSOLE)) {
			if (commandManager.getConsoleClass().isInstance(executor)) {
				internalExecuteConsole(commandManager.getConsoleClass().cast(executor), arguments);
				return;
			}
			commandManager.sendMessage(executor, "This command can only be executed by a console.");
			return;
		}

		if (commandManager.getSenderClass().isInstance(executor)) {
			internalExecuteCommon(commandManager.getSenderClass().cast(executor), arguments);
			return;
		}

		Logger.error(executor.getClass() + " is not a known executor type for the current platform.");
	}


	protected abstract void internalExecutePlayer(@NotNull Object player, @NotNull List<String> arguments);

	protected abstract void internalExecuteConsole(@NotNull Object console, @NotNull List<String> arguments);

	protected abstract void internalExecuteCommon(@NotNull Object sender, @NotNull List<String> arguments);

	public @NotNull String getPermission() {
		if (isRootCommand()) {
			return commandManager.getConfig().basePermission + "." + annotation.aliases()[0];
		}

		CommonCommand command = commandManager.getCommand(getParent());

		if (command == null) {
			throw new RuntimeException("Command " + getParent().getName() + " is not registered.");
		}

		return command.getPermission() + "." + annotation.aliases()[0];
	}

	public final @NotNull List<CommonCommand> getPrimitiveSubCommands() {
		return this.subCommands;
	}

	public List<String> getArguments() {
		return List.of(annotation.arguments());
	}

	public Class<? extends CommonCommand> getParent() {
		return annotation.parent();
	}

	public boolean isRootCommand() {
		return getParent().equals(CommonCommand.class);
	}

	public final String getMainAlias() {
		return annotation.aliases()[0];
	}

	public final List<String> getAliases() {
		return List.of(annotation.aliases());
	}

	protected final void sendMessage(Object target, GenericMessageBuilder<?> message) {
		sendMessage(target, message.toString());
	}

	protected final void sendMessage(Object target, String message) {
		commandManager.sendMessage(target, message);
	}

	protected final boolean checkPermission(Object target, String permission) {
		return commandManager.checkPermission(target, permission);
	}

}
