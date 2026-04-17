package gg.mmorealms.module.legendaries.velocity.command;


import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.legendaries.velocity.config.LegendarySpawnConfig;
import gg.mmorealms.module.legendaries.velocity.manager.LegendaryLifecycleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@Command(aliases = {"spawnlegendary", "legendaryspawn"}, arguments = {"canSpawnOnEmptyServer"})
public class LegendarySpawnCommand extends VelocityCommand {
	private @Inject LegendarySpawnConfig config;
	private @Inject LegendaryLifecycleManager lifecycleManager;

	public LegendarySpawnCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return List.of("true", "false");
	}

	@Override
	protected void executeConsole(ConsoleCommandSource console, List<String> arguments) {
		executeSpawn(console, arguments, null);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		executeSpawn(player, arguments, player.getUniqueId());
	}

	protected void executeSpawn(Object sender, List<String> arguments, @Nullable UUID senderUUID) {
		Logger.debug("Executing legendary spawn command with arguments: " + arguments);

		boolean canSpawnOnEmpty = false;
		if (!arguments.isEmpty()) {
			String argument = arguments.getFirst();
			canSpawnOnEmpty = Boolean.parseBoolean(argument);
		}

		boolean failed = !lifecycleManager.sendSpawnRequest(canSpawnOnEmpty, senderUUID);
		if (failed) {
			Logger.debug("Failed to send legendary spawn request");
			sendMessage(sender, config.lang.noPlayersOnWilds);
			return;
		}

		Logger.debug("Sent legendary spawn request, spawning legendary...");
		sendMessage(sender, config.lang.spawningLegendary);
	}
}


