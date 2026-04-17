package command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.exception.CommandNotAnnotated;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import dto.CommandSender;
import dto.SimpleCommand;

import java.util.List;

@Command(aliases = "sub2", arguments = {"s1", "s2"}, parent = BaseCommand.class)
public class SubCommand2 extends SimpleCommand {
	public SubCommand2(CommonCommandManager commandManager) throws CommandNotAnnotated {
		super(commandManager);
	}

	@Override
	protected void execute(CommandSender sender, List<String> arguments) {

	}
}
