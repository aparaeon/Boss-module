package gg.mmorealms.module.pokemon.backend.fabric.Command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.EndBattleEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = "end_battle", onlyFor = Command.OnlyFor.PLAYERS)
public class EndBattleCommand extends UserCommand {
	public EndBattleCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> list) {
		new EndBattleEvent(user.getPlayer()).fireAsync();
	}
}
