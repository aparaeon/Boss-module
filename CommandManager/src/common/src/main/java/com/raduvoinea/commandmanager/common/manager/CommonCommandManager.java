package com.raduvoinea.commandmanager.common.manager;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.commandmanager.common.config.CommandManagerConfig;
import com.raduvoinea.commandmanager.common.exception.CommandNotAnnotated;
import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.utils.dependency_injection.Injector;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.reflections.Reflections;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class CommonCommandManager {

	private final Reflections.Crawler reflectionsCrawler;
	private final List<CommonCommand> commands = new ArrayList<>();
	private final Class<?> playerClass;
	private final Class<?> consoleClass;
	private final Class<?> senderClass;
	private final CommandManagerConfig config;
	private final Holder<Injector> injectorHolder;

	public CommonCommandManager(@NotNull Reflections.Crawler reflectionsCrawler, @NotNull Class<?> playerClass,
	                            @NotNull Class<?> consoleClass, @NotNull Class<?> senderClass,
	                            @NotNull CommandManagerConfig config, @NotNull Holder<Injector> injectorHolder) {
		this.reflectionsCrawler = reflectionsCrawler;
		this.playerClass = playerClass;
		this.consoleClass = consoleClass;
		this.senderClass = senderClass;
		this.config = config;
		this.injectorHolder = injectorHolder;
	}

	public <T extends CommonCommand> void register(Command commandAnnotation, @NotNull T command) {
		try {
			if (commandAnnotation != null && commandAnnotation.parent() != CommonCommand.class) {
				return;
			}

			if (!injectorHolder.isEmpty()) {
				injectorHolder.value().inject(command);
			}

			register(command);
		} catch (Throwable error) {
			// Do not print NotAnnotated error as errors, but was warnings. There are legitimate uses of it not being
			// annotated, but it might also be an oversight that needs to be addressed.
			if (error instanceof CommandNotAnnotated) {
				Logger.warn(error.getMessage());
				return;
			}

			if (error instanceof InvocationTargetException invocationError) {
				if (invocationError.getTargetException() instanceof CommandNotAnnotated) {
					Logger.warn(invocationError.getTargetException().getMessage());
					return;
				}
			}

			Logger.error(error);
		}
	}

	public void register(@NotNull Class<? extends CommonCommand> commandClass) {
		register(commandClass.getAnnotation(Command.class), commandClass);
	}

	public void register(Command commandAnnotation, @NotNull Class<? extends CommonCommand> commandClass) {
		try {
			if (commandAnnotation != null && commandAnnotation.parent() != CommonCommand.class) {
				return;
			}

			if (Modifier.isAbstract(commandClass.getModifiers())) {
				return;
			}

			CommonCommand command = commandClass.getConstructor(CommonCommandManager.class).newInstance(this);
			if (!injectorHolder.isEmpty()) {
				injectorHolder.value().inject(command);
			}
			register(command);
		} catch (Throwable error) {
			// Do not print NotAnnotated error as errors, but was warnings. There are legitimate uses of it not being
			// annotated, but it might also be an oversight that needs to be addressed.
			if (error instanceof CommandNotAnnotated) {
				Logger.warn(error.getMessage());
				return;
			}

			if (error instanceof InvocationTargetException invocationError) {
				if (invocationError.getTargetException() instanceof CommandNotAnnotated) {
					Logger.warn(invocationError.getTargetException().getMessage());
					return;
				}
			}

			Logger.error(error);
			Logger.error("There was an error while registering command " + commandClass.getName());
		}
	}

	private void register(@NotNull CommonCommand command) {
		Logger.log("Registering command(s) " + command.getAliases());
		commands.add(command);
		platformRegister(command);
	}

	public @Nullable CommonCommand getCommand(@NotNull Class<? extends CommonCommand> commandClass) {
		for (CommonCommand command : commands) {
			if (command.getClass().equals(commandClass)) {
				return command;
			}

			for (CommonCommand subCommand : command.getPrimitiveSubCommands()) {
				if (subCommand.getClass().equals(commandClass)) {
					return subCommand;
				}
			}
		}

		return null;
	}

	public boolean checkPermission(Object target, String permission) {
		if (consoleClass.isInstance(target)) {
			return true;
		}
		if (playerClass.isInstance(target)) {
			return LuckPermsUtils.checkPermission(playerClass, playerClass.cast(target), permission);
		}
		return LuckPermsUtils.checkPermission(playerClass, senderClass.cast(target), permission);
	}

	protected abstract void platformRegister(@NotNull CommonCommand command);

	public abstract void sendMessage(Object target, String message);

}
