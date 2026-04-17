package gg.mmorealms.module.legendaries.velocity.command;


import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.legendaries.velocity.config.LegendarySpawnConfig;
import gg.mmorealms.module.legendaries.velocity.manager.LegendaryInfoManager;
import gg.mmorealms.module.legendaries.velocity.manager.LegendaryLifecycleManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command(aliases = {"despawnlegendary", "legendarydespawn"}, arguments = {"pokemon"})
public class LegendaryDespawnCommand extends VelocityCommand {
	private @Inject LegendarySpawnConfig config;
	private @Inject LegendaryInfoManager infoManager;
	private @Inject LegendaryLifecycleManager lifecycleManager;

	public LegendaryDespawnCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return infoManager.getAll().stream()
				.map(info -> info.getPokemonName() + " " + info.getPokemonUUID().toString())
				.toList();
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		Logger.debug("Executing legendary despawn command...");

		if (arguments.isEmpty()) {
			return;
		}

		try {
			UUID pokemonUUID = UUID.fromString(arguments.getLast());

			boolean failed = !lifecycleManager.sendDespawnRequest(pokemonUUID);
			if (failed) {
				sendMessage(sender, config.lang.wrongUUID);
				return;
			}

			sendMessage(sender, config.lang.despawningLegendary);
		} catch (IllegalArgumentException e) {
			sendMessage(sender, config.lang.wrongUUID);
		}
	}
}


