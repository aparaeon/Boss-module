package gg.mmorealms.module.warps.backend.common.command;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.warps.backend.common.config.WarpsConfig;
import gg.mmorealms.module.warps.backend.common.dto.Warp;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"warp"}, arguments = {"location"}, onlyFor = Command.OnlyFor.PLAYERS)
@Getter
@Setter
public class WarpCommand extends UserCommand {

	private @Inject WarpsConfig config;

	public WarpCommand(CommonCommandManager commandManager) {
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
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String warpName = arguments.getFirst();

		Warp warp = config.getWarp(warpName);

		if (warp == null) {
			user.sendMessage(config.lang.invalidWarp);
			return;
		}

		user.send(IServerLocation.of(warp.getServerType()), warp.getLocation());
		user.sendMessage(config.lang.successWarp.parse("name", warp.getName()));
	}
}
