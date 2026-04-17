package command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.exception.CommandNotAnnotated;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import dto.CommandSender;
import dto.SimpleCommand;

import java.util.List;

@Command(aliases = "subsub1", arguments = {"ss1", "ss2"}, parent = SubCommand1.class)
public class SubSubCommand1 extends SimpleCommand {
	public SubSubCommand1(CommonCommandManager commandManager) throws CommandNotAnnotated {
		super(commandManager);
	}

	@Override
	protected void execute(CommandSender sender, List<String> arguments) {

	}
}
