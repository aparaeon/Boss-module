package gg.mmorealms.module.kits.backend.common.command;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import gg.mmorealms.module.kits.backend.common.exception.ClaimKitException;
import gg.mmorealms.module.kits.backend.common.gui.KitGUI;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"kit"}, arguments = {"?name"}, onlyFor = Command.OnlyFor.PLAYERS)
public class KitCommand extends UserCommand {
	public KitCommand(CommonCommandManager commandManager) {
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
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String kitName = arguments.getFirst();
		if (kitName == null) {
			new KitGUI(user).open();
			return;
		}

		Kit kit = KitUtils.get(kitName);

		if (kit == null) {
			user.sendMessage("Kit not found"); // TODO Config
			return;
		}

		try {
			kit.claimKit(user, false);
		} catch (ClaimKitException exception) {
			user.sendMessage(exception.getMessage());
		}
	}
}
