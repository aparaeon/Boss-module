package gg.mmorealms.module.wild.backend.common.command;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.LevelType;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.wild.backend.common.WildBackendModule;
import gg.mmorealms.module.wild.backend.common.WildConfig;
import gg.mmorealms.module.wild.backend.common.dto.event.TeleportToRandomLocationEvent;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"wild", "rtp"}, arguments = {"?dimension"}, onlyFor = Command.OnlyFor.PLAYERS)
public class WildCommand extends UserCommand {

	public WildCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return LevelType.getSuggestionList();
	}

	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		WildConfig config = WildBackendModule.instance().getConfig();
		IBackendCooldowns cooldowns = IBackendCooldowns.getByUser(user);

		String dimensionName = "overworld";
		if (!arguments.isEmpty() && arguments.getFirst() != null && !arguments.getFirst().isEmpty()) {
			dimensionName = arguments.getFirst();
		}

		LevelType world = LevelType.get(dimensionName);

		String cooldownName = WildConfig.RTP_COOLDOWN_TEMPLATE.parse("dimension", world.getFriendlyNames().getFirst()).parse();
		if (cooldowns.isActive(cooldownName)) {
			user.sendMessage(config.lang.activeRtpCooldown
					.parse("cooldown", cooldowns.getFormattedTime(cooldownName))
					.parse("dimension", world.getFriendlyNames().getFirst()));
			return;
		}

		cooldowns.set(cooldownName, config.rtpCooldownMap.get(world));

		user.send(IServerLocation.of(ServerType.WILD), false,
				new TeleportToRandomLocationEvent(user.getUUID(), world));
	}
}
