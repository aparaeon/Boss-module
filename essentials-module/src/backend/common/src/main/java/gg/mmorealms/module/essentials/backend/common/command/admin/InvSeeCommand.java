package gg.mmorealms.module.essentials.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.essentials.backend.common.gui.InvSeeGUI;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"inventory_see", "inv_see", "invsee"}, onlyFor = Command.OnlyFor.PLAYERS, arguments = {"target"})
public class InvSeeCommand extends UserCommand {

	public InvSeeCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String targetUsername = arguments.getFirst();

		IUser target = IUser.getByUsername(targetUsername); // TODO change to IUser

		if (target == null) {
			user.sendMessage("User not found"); // TODO Config
			return;
		}

		if (!target.isOnlineOnNetwork()) {
			user.sendMessage("User is not online"); // TODO Config
			return;
		}

		new InvSeeGUI(user, target.getUUID()).open();
	}

}
