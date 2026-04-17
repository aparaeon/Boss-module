package gg.mmorealms.module.crates.backend.common.command.admin.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.crates.backend.common.command.admin.AdminCommand;
import gg.mmorealms.module.crates.backend.common.config.CratesConfig;
import gg.mmorealms.module.crates.backend.common.database.ICrateKeys;
import gg.mmorealms.module.crates.backend.common.dto.Crate;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"give_virtual_key"}, parent = AdminCommand.class, arguments = {"player", "amount", "type"})
public class GiveVirtualKeyCommand extends BackendCommand {

	private @Inject CratesConfig config;
	private @Inject MinecraftServer server;

	public GiveVirtualKeyCommand(CommonCommandManager commandManager) {
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
		int amount;
		try {
			amount = Integer.parseInt(arguments.get(1));
		} catch (NumberFormatException e) {
			sendMessage(sender, "Invalid amount");// TODO Config
			return;
		}

		if (amount <= 0) {
			sendMessage(sender, "<red>Please use \"/crates admin remove_virtual_key\" instead if you want to remove keys");
			return;
		}

		String type = arguments.get(2);

		if (config.crates.stream().noneMatch(crate -> crate.getId().equals(type))) {
			sendMessage(sender, "Invalid crate type"); // TODO Config
			return;
		}

		IUser.executeForUser(targetUsername, (target) -> {
			execute(sender, target, amount, type);
		}, () -> {
			sendMessage(sender, "Player not found"); // TODO Config
		});
	}

	private void execute(CommandSource sender, IUser target, int amount, String type) {
		ICrateKeys crateKeys = ICrateKeys.getByUser(target);

		crateKeys.addKeys(type, amount);
		sendMessage(sender, new MessageBuilder("You have given {amount} {type} keys from {player}")
				.parse("amount", amount)
				.parse("type", type)
				.parse("player", target.getUsername())
				.parse());
	}
}
