package gg.mmorealms.module.realms.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.realms.velocity.config.RealmsConfig;
import gg.mmorealms.module.realms.velocity.manager.RealmsManager;

import java.util.List;
import java.util.UUID;

@Command(aliases = "force_unclaim_realm")
public class ForceUnclaimRealmCommand extends VelocityCommand {
	private @Inject RealmsConfig config;
	private @Inject RealmsManager realmsManager;

	public ForceUnclaimRealmCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String ownerUUIDString = arguments.getFirst();
		UUID ownerUUID;

		try {
			ownerUUID = UUID.fromString(ownerUUIDString);
		} catch (Throwable e) {
			sendMessage(sender, config.lang.invalidUUID
					.parse("uuid", ownerUUIDString)
			);
			return;
		}

		realmsManager.setRealmState("proxy", ownerUUID, null);
		sendMessage(sender, config.lang.forceUnlock
				.parse("user", ownerUUID)
		);
	}
}
