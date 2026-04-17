package gg.mmorealms.module.essentials.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = "execute_multiple_times", arguments = {"count", "command..."})
public class ExecuteMultipleTimes extends BackendCommand {

	private @Inject MinecraftServer server;

	public ExecuteMultipleTimes(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("target")) {
			return recommendPlayersList();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String countString = arguments.get(0);
		String command = arguments.get(1);

		int count;

		try {
			count = Integer.parseInt(countString);
		} catch (NumberFormatException e) {
			sendMessage(sender, "Invalid number");
			return;
		}

		for (int i = 0; i < count; i++) {
			switch (sender) {
				case MinecraftServer srv -> {
					server.getCommands().performPrefixedCommand(srv.createCommandSourceStack(), command);
				}
				case ServerPlayer player -> {
					CommandSourceStack sourceStack = player.createCommandSourceStack();
					server.getCommands().performPrefixedCommand(sourceStack, command);
				}
				default -> {
					sendMessage(sender, "Invalid command source " + sender.getClass());
					return;
				}
			}
		}
	}
}
