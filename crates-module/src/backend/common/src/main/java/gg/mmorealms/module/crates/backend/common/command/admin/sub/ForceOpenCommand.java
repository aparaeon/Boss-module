package gg.mmorealms.module.crates.backend.common.command.admin.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.exceptions.PermissionException;
import gg.mmorealms.module.crates.backend.common.command.admin.AdminCommand;
import gg.mmorealms.module.crates.backend.common.config.CratesConfig;
import gg.mmorealms.module.crates.backend.common.dto.Crate;
import gg.mmorealms.module.crates.backend.common.gui.RouletteCrateGUI;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"force_open"}, parent = AdminCommand.class, arguments = {"player", "id"})
public class ForceOpenCommand extends BackendCommand {

	private @Inject CratesConfig config;
	private @Inject MinecraftServer server;

	public ForceOpenCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("player")) {
			return recommendPlayersList();
		}

		if (argument.equals("type")) {
			return config.crates.stream().map(Crate::getId).toList();
		}

		return List.of();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String targetUsername = arguments.get(0);
		String id = arguments.get(1);

		IUser targetOffline = IUser.getByUsername(targetUsername);

		if (targetOffline == null) {
			sendMessage(sender, "Player not found"); // TODO Config
			return;
		}

		if (!(targetOffline instanceof User target)) {
			sendMessage(sender, "Target user is not online on the current server. Use /execute_on_backend " +
					"to run this command on the server the player is online."); // TODO Config
			return;
		}

		Crate crate = config.getCrate(id);

		if (crate == null) {
			sendMessage(sender, "Crate not found"); // TODO Config
			return;
		}

		try {
			new RouletteCrateGUI(target, crate).open();
		} catch (PermissionException exception) {
			sendMessage(sender, "The target does not have permission to open this crate"); // TODO Config
		}
	}


}
