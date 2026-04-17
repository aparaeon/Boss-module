package gg.mmorealms.module.homes.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.homes.backend.common.command.HomesCommand;
import gg.mmorealms.module.homes.backend.common.utils.HomesUtils;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Setter
@Command(aliases = {"add"}, arguments = {"name"}, parent = HomesCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class AddCommand extends UserCommand {
	public AddCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String name = arguments.getFirst();

		HomesUtils.addHome(user, name);
	}
}
