package gg.mmorealms.module.realms.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.gui.RealmCreateGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"create"}, onlyFor = Command.OnlyFor.PLAYERS, parent = RealmCommand.class)
public class CreateCommand extends UserCommand {

	public CreateCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new RealmCreateGUI(user);
	}
}
