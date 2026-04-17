package gg.mmorealms.module.realms.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.gui.PagedMessageGUI;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"help"}, arguments = {"?page"}, parent = RealmCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class HelpCommand extends UserCommand {

	private @Inject RealmsConfig config;

	private static PagedMessageGUI pagedMessage = null;

	public HelpCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		if (pagedMessage == null) {
			pagedMessage = new PagedMessageGUI(config.lang.helpMessage, config.helpPageConfig, "/realm help");
		}

		pagedMessage.send(user.getPlayer(), arguments.getFirst());
	}
}
