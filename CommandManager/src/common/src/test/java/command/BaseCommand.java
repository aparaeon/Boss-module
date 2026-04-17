package command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.exception.CommandNotAnnotated;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import dto.CommandSender;
import dto.SimpleCommand;

import java.util.List;

@Command(aliases = "base", arguments = {"b1", "b2"})
public class BaseCommand extends SimpleCommand {
	public BaseCommand(CommonCommandManager commandManager) throws CommandNotAnnotated {
		super(commandManager);
	}

	@Override
	protected void execute(CommandSender sender, List<String> arguments) {

	}
}
