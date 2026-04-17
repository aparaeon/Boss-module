package gg.mmorealms.module.kits.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import gg.mmorealms.module.kits.backend.common.exception.ClaimKitException;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = "force_claim", arguments = {"target", "kit"}, parent = AdminCommand.class)
public class ForceClaim extends BackendCommand {

	public ForceClaim(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return switch (argument) {
			case "target" -> recommendPlayersList();
			case "kit" -> KitUtils.getAllNames();
			default -> List.of();
		};
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String targetUsername = arguments.get(0);
		String kitName = arguments.get(1);

		IUser target = IUser.getByUsername(targetUsername);
		if (target == null) {
			return;
		}

		Kit kit = KitUtils.get(kitName);

		if (kit == null) {
			sendMessage(sender, "Kit not found"); // TODO Config
			return;
		}

		try {
			kit.claimKit(target, true);
		} catch (ClaimKitException exception) {
			sendMessage(sender, exception.getMessage());
		}
	}
}
