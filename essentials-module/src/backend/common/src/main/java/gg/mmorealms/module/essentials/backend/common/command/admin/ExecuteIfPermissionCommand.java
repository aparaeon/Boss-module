package gg.mmorealms.module.essentials.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.essentials.backend.common.EssentialsBackendModule;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = "execute_if_permission", arguments = {"target", "permission", "commands..."})
public class ExecuteIfPermissionCommand extends BackendCommand {

	public ExecuteIfPermissionCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("target")) {
			return recommendPlayersList();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String targetUsername = arguments.get(0);
		String permission = arguments.get(1);
		String commands = String.join(" ", arguments.subList(2, arguments.size()));

		if (!commands.contains("|")) {
			sendMessage(sender, "Commands must be separated by a pipe (|).");
			return;
		}

		String hasPermissionCommand = commands.split("\\|")[0].trim();
		String noPermissionCommand = commands.split("\\|")[1].trim();

		IUser targetUser = IUser.getByUsername(targetUsername);

		if (targetUser == null) {
			sendMessage(sender, "User not found: " + targetUsername);
			return;
		}

		if (targetUser.hasPermission(permission)) {
			EssentialsBackendModule.instance().executeCommand(hasPermissionCommand);
			sendMessage(sender, "Executed command with permission");
		} else {
			EssentialsBackendModule.instance().executeCommand(noPermissionCommand);
			sendMessage(sender, "Executed command with no permission");
		}

	}
}
