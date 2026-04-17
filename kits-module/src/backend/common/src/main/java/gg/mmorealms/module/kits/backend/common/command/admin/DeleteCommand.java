package gg.mmorealms.module.kits.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"delete"}, arguments = {"name"}, parent = AdminCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class DeleteCommand extends BackendCommand {

	private @Inject KitsConfig config;

	public DeleteCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("name")) {
			return KitUtils.getAllNames();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executePlayer(@NotNull ServerPlayer player, @NotNull List<String> arguments) {
		String name = arguments.getFirst();
		boolean result = KitUtils.remove(name);

		if (!result) {
			sendMessage(player, config.lang.notFoundKit.parse("name", name).parse());
		} else {
			sendMessage(player, config.lang.successDeleteMessage);
		}
	}

}
