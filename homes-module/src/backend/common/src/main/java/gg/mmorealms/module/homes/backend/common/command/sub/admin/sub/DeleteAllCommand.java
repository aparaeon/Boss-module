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

import java.util.List;

@Command(aliases = "delete_all", arguments = {"username"}, parent = AdminCommand.class, onlyFor = Command.OnlyFor.BOTH)
public class DeleteAllCommand extends BackendCommand {
	private @Inject HomesConfig config;

	public DeleteAllCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String username = arguments.getFirst();

		IHomes homes = IHomes.getByUsername(username);
		if (homes == null) {
			sendMessage(sender, config.lang.noHomeMessageListCommand.parse("user", username));
			return;
		}

		homes.removeAll();
		sendMessage(sender, config.lang.successDeleteAllAdminCommand.parse("user", username));

	}
}
