package gg.mmorealms.module.essentials.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.essentials.backend.common.gui.EnderChestSeeGUI;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"ender_chest_see"}, onlyFor = Command.OnlyFor.PLAYERS, arguments = {"target"})
public class EnderChestSeeCommand extends UserCommand {

	public EnderChestSeeCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String targetUsername = arguments.getFirst();

		IUser target = IUser.getByUsername(targetUsername);

		if (target == null) {
			user.sendMessage("User not found"); // TODO Config
			return;
		}

		if (!target.isOnlineOnNetwork()) {
			user.sendMessage("User is not online"); // TODO Config
			return;
		}

		new EnderChestSeeGUI(user, target.getUUID()).open();
	}

}
