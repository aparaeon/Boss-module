package gg.mmorealms.module.realms.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.realms.velocity.RealmsVelocityModule;
import gg.mmorealms.module.realms.velocity.config.RealmsConfig;

import java.util.List;

@Command(aliases = "check_claimed_realms")
public class CheckClaimedRealmsCommand extends VelocityCommand {
	private @Inject RealmsConfig config;

	public CheckClaimedRealmsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		Logger.debug(new MessageBuilder("{realms_claimed_realms_on_proxy}")
				.parse("realms_claimed_realms_on_proxy", RealmsVelocityModule.instance().getRealmsManager().getRealmMap()));
	}
}