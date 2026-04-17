package gg.mmorealms.module.kits.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"info"}, arguments = {"name"}, parent = AdminCommand.class)
public class InfoCommand extends BackendCommand {
	private @Inject KitsConfig config;

	public InfoCommand(CommonCommandManager commandManager) {
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
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String kitName = arguments.getFirst();

		Kit kit = KitUtils.get(kitName);
		if (kit == null) {
			sendMessage(sender, config.lang.notFoundKit.parse("name", kitName));

			return;
		}

		sendMessage(sender, kit.toDetailedString());
	}
}
