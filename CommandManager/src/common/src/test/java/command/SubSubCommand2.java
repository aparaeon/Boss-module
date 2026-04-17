package command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.exception.CommandNotAnnotated;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import dto.CommandSender;
import dto.SimpleCommand;

import java.util.List;

@Command(aliases = "subsub2", arguments = {"ss1", "ss2"}, parent = SubCommand2.class)
public class SubSubCommand2 extends SimpleCommand {
	public SubSubCommand2(CommonCommandManager commandManager) throws CommandNotAnnotated {
		super(commandManager);
	}

	@Override
	protected void execute(CommandSender sender, List<String> arguments) {

	}
}
