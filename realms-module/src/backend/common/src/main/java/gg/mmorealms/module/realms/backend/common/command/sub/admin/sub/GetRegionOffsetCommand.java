package gg.mmorealms.module.realms.backend.common.command.sub.admin.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.realms.backend.common.command.sub.admin.AdminCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"get_region_offset"}, onlyFor = Command.OnlyFor.PLAYERS, parent = AdminCommand.class)
public class GetRegionOffsetCommand extends UserCommand {

	private @Inject RealmsConfig config;

	public GetRegionOffsetCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		Location userLocation = user.getLocation();

		user.sendMessage(new MessageBuilder("({x} {y} {z})")
				.parse("x", userLocation.getX() % 512)
				.parse("y", userLocation.getY() % 512)
				.parse("z", userLocation.getZ() % 512)
				.parse()
		);
	}


}
