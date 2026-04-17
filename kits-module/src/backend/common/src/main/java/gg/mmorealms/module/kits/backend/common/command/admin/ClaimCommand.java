package gg.mmorealms.module.kits.backend.common.command.admin;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import gg.mmorealms.module.kits.backend.common.exception.ClaimKitException;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"claim"}, arguments = {"kit", "user"}, parent = AdminCommand.class)
public class ClaimCommand extends BackendCommand {
	public ClaimCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String kitName = arguments.get(0);
		String userName = arguments.get(1);
		IUser user = IUser.getByUsername(userName);

		if (user == null) {
			return;
		}

		Kit kit = KitUtils.get(kitName);

		if (kit == null) {
			return;
		}

		try {
			kit.claimKit(user, false);
		} catch (ClaimKitException ignored) {
		}
	}
}