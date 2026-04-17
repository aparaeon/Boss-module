package gg.mmorealms.module.realms.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"legendarycapture"}, arguments = {"isShared"}, onlyFor = Command.OnlyFor.PLAYERS, parent = RealmCommand.class)
public class LegendaryCaptureBackendCommand extends UserCommand {

	private @Inject RealmsConfig config;

	public LegendaryCaptureBackendCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return List.of("shared", "ownerOnly");
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_LEGENDARY_CAPTURE_SHARED);

		if (realm == null) {
			return;
		}

		if (arguments.isEmpty()) {
			return;
		}

		String argument = arguments.getFirst();
		if (argument == null) {
			return;
		}

		boolean isMembersCaptureLegendary;
		if (argument.equals("shared")) {
			isMembersCaptureLegendary = true;
		} else if (argument.equals("ownerOnly")) {
			isMembersCaptureLegendary = false;
		} else {
			return;
		}

		user.sendMessage(isMembersCaptureLegendary
				? config.lang.legendaryCaptureShared
				: config.lang.legendaryCaptureOwnerOnly);

		realm.setLegendaryCaptureShared(isMembersCaptureLegendary);
	}
}
