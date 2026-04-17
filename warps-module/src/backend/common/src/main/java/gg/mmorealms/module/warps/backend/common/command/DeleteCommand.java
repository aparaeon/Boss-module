package gg.mmorealms.module.warps.backend.common.command;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.warps.backend.common.config.WarpsConfig;
import gg.mmorealms.module.warps.backend.common.dto.Warp;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"delete"}, arguments = {"location"}, parent = WarpsCommand.class)
public class DeleteCommand extends BackendCommand {
	private @Inject WarpsConfig config;

	public DeleteCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("location")) {
			return config.getWarpNames();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String name = arguments.getFirst();
		Warp toDelete = null;

		for (Warp warp : config.warps) {
			if (warp.getName().equals(name)) {
				toDelete = warp;
				break;
			}
		}

		if (toDelete == null) {
			sendMessage(sender, config.lang.invalidWarp);
			return;
		}

		config.warps.remove(toDelete);

		sendMessage(sender, config.lang.successDelete);
	}

}
