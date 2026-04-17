package gg.mmorealms.module.homes.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.homes.backend.common.command.HomesCommand;
import gg.mmorealms.module.homes.backend.common.config.HomesConfig;
import gg.mmorealms.module.homes.backend.common.dto.IHomes;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"delete"}, arguments = {"name"}, parent = HomesCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class DeleteCommand extends UserCommand {
	private @Inject HomesConfig config;

	public DeleteCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("name")) {
			return IHomes.getByUUID(context.getSource().getPlayer().getUUID()).getNames();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String name = arguments.getFirst();

		IHomes homes = IHomes.getByUser(user);
		if (!homes.remove(name)) {
			user.sendMessage(config.lang.noHomeDeleteCommand);
			return;
		}

		user.sendMessage(config.lang.successDeleteCommand.parse("name", name));
	}
}
