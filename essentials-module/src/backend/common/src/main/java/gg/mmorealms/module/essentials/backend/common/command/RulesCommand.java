package gg.mmorealms.module.essentials.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.gui.PagedMessageGUI;
import gg.mmorealms.module.essentials.backend.common.config.EssentialsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"rules"}, arguments = {"?page"}, onlyFor = Command.OnlyFor.PLAYERS)
public class RulesCommand extends UserCommand {
	private @Inject EssentialsConfig config;

	private static PagedMessageGUI pagedMessage = null;

	public RulesCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}


	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		if (pagedMessage == null) {
			pagedMessage = new PagedMessageGUI(config.rules, config.rulesPageConfig, "/rules");
		}

		pagedMessage.send(user.getPlayer(), arguments.getFirst());
	}
}
