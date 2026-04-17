package gg.mmorealms.module.realms.backend.common.command.sub.admin.sub;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.sub.admin.AdminCommand;
import gg.mmorealms.module.realms.backend.common.gui.RealmGUI;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = "force_create", arguments = {"user"}, parent = AdminCommand.class)
public class ForceCreateCommand extends BackendCommand {
	public ForceCreateCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String username = arguments.getFirst();

		IUser user = IUser.getByUsername(username);
		if (user == null) {
			return;
		}

		new RealmGUI((User) user, false);
	}
}
