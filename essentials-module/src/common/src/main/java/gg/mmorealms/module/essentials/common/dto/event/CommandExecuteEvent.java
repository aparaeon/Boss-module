package gg.mmorealms.module.essentials.common.dto.event;


import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
// Why is there a no arg constructor here?
@NoArgsConstructor
public class CommandExecuteEvent extends NetworkEvent {

	private @NotNull String command;
	private @Nullable String executeAs;

	private CommandExecuteEvent(@NotNull String target, @NotNull String command, @Nullable String executeAs) {
		super(target);
		this.command = command;
		this.executeAs = executeAs;
	}

	public static CommandExecuteEvent onBackend(@NotNull String backend, @NotNull String command) {
		return onBackend(backend, command, null);
	}

	public static CommandExecuteEvent onBackend(@NotNull String backend, @NotNull String command, @Nullable String executeAs) {
		return new CommandExecuteEvent(backend, command, executeAs);
	}

	public static CommandExecuteEvent onProxy(@NotNull String command) {
		return onProxy(command, null);
	}

	public static CommandExecuteEvent onProxy(@NotNull String command, @Nullable String executeAs) {
		return new CommandExecuteEvent(CommonLoader.PROXY_ID, command, executeAs);
	}

}
