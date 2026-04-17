package gg.mmorealms.module.realms.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.ConfirmationGUI;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"delete"}, onlyFor = Command.OnlyFor.PLAYERS, parent = RealmCommand.class)
public class DeleteCommand extends UserCommand {

	public DeleteCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_DELETE);

		if (realm == null) {
			return;
		}

		new ConfirmationGUI(user) {
			@Override
			protected void onConfirm(ClickType click) {
				realm.delete();
				this.close();
				Logger.info(new MessageBuilder("{owner_uuid} deleted his own realm")
						.parse("owner_uuid", user.getUUID())
				);
			}
		}.open();
	}


}
