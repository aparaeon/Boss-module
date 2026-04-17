package gg.mmorealms.module.core.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.core.velocity.config.CoreConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = "cache_commit_proxy")
public class CommitCacheProxyCommand extends VelocityCommand {

	private @Inject CoreConfig config;

	public CommitCacheProxyCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		sendMessage(sender, config.lang.commitingCache);

		for (DatabaseLoader<?, ?, ?> databaseLoader : DatabaseLoader.getALL()) {
			databaseLoader.getCache().saveCache(false);
		}

		sendMessage(sender, "Cache committed successfully!");
	}
}
