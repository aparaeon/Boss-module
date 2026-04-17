package gg.mmorealms.module.realms.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.realms.velocity.RealmsVelocityModule;
import gg.mmorealms.module.realms.velocity.config.RealmsConfig;
import gg.mmorealms.module.realms.velocity.dto.ProxyRealm;

import java.util.List;
import java.util.UUID;

@Command(aliases = "check_realm", arguments = {"ownerUUID"})
public class CheckRealmCommand extends VelocityCommand {
	private @Inject RealmsConfig config;

	public CheckRealmCommand(CommonCommandManager commandManager) {
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

		ProxyRealm proxyRealm = RealmsVelocityModule.instance().getRealmsManager().getProxyRealm(ownerUUID);

		if (proxyRealm == null) {
			sendMessage(sender, "This realm is not loaded");
			return;
		}

		sendMessage(sender, config.lang.checkRealm
				.parse("user", ownerUUID)
				.parse("server", proxyRealm.getServerID())
				.parse("state", proxyRealm.getState())
		);
	}
}
