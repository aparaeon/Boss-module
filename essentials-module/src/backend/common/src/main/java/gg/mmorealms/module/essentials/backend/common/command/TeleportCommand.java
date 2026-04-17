package gg.mmorealms.module.essentials.backend.common.command;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"teleport", "teleport_to", "mmo_tp"}, arguments = {"player"}, onlyFor = Command.OnlyFor.PLAYERS)
public class TeleportCommand extends UserCommand {

	public TeleportCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String playerUsername = arguments.getFirst();

		IUser target = IUser.getByUsername(playerUsername);

		if (target == null) {
			user.sendMessage("Player not found."); // TODO Lang
			return;
		}

		user.send(target.getServerLocation(), target.getBlockLocation());
	}

}
