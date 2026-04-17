package gg.mmorealms.module.essentials.backend.common.command.admin.gamemode;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"gamemode_adventure", "gma"}, onlyFor = Command.OnlyFor.PLAYERS)
public class GamemodeAdventureCommand extends UserCommand {

	public GamemodeAdventureCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		user.getPlayer().setGameMode(GameType.ADVENTURE);
		user.sendMessage("Your game mode has been set to Adventure."); // TODO: Use Config for messages
	}

}
