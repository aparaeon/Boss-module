package dto;

import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.commandmanager.common.exception.CommandNotAnnotated;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SimpleCommand extends CommonCommand {
	public SimpleCommand(CommonCommandManager commandManager) throws CommandNotAnnotated {
		super(commandManager);
	}

	@Override
	protected void internalExecutePlayer(@NotNull Object player, @NotNull List<String> arguments) {
		execute((CommandSender) player, arguments);
	}

	@Override
	protected void internalExecuteConsole(@NotNull Object console, @NotNull List<String> arguments) {
		execute((CommandSender) console, arguments);
	}

	@Override
	protected void internalExecuteCommon(@NotNull Object sender, @NotNull List<String> arguments) {
		execute((CommandSender) sender, arguments);
	}

	protected abstract void execute(CommandSender sender, List<String> arguments);
}
