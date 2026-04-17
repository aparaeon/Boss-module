package gg.mmorealms.module.homes.backend.common.command.sub.admin.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.homes.backend.common.command.sub.admin.AdminCommand;
import gg.mmorealms.module.homes.backend.common.config.HomesConfig;
import gg.mmorealms.module.homes.backend.common.dto.IHomes;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = "delete", arguments = {"username", "home"}, parent = AdminCommand.class)
public class DeleteCommand extends BackendCommand {
	private @Inject HomesConfig config;

	public DeleteCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("username")) {
			return recommendPlayersList();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String username = arguments.get(0);
		String homeName = arguments.get(1);

		IHomes homes = IHomes.getByUsername(username);
		if (homes == null) {
			sendMessage(sender, config.lang.noHomeMessageListCommand.parse("user", username));
			return;
		}

		if (!homes.remove(homeName)) {
			sendMessage(sender, config.lang.noHomeWithThisNameAdminCommand
					.parse("user", username)
					.parse("name", homeName)
			);
			return;
		}

		sendMessage(sender, config.lang.successDeleteAdminCommand
				.parse("user", username)
				.parse("name", homeName)
		);
	}
}
