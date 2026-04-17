package gg.mmorealms.module.core.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.core.common.dto.event.server.CommitCacheBroadcast;
import gg.mmorealms.module.core.velocity.config.CoreConfig;

import java.util.List;

@Command(aliases = {"commit_all_cache"})
public class CommitAllCacheCommand extends VelocityCommand {

	private @Inject CoreConfig config;

	public CommitAllCacheCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		sendMessage(sender, config.lang.commitingCache);

		new CommitCacheBroadcast(false).send();
	}
}
