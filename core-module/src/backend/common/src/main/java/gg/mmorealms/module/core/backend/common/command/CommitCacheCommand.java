package gg.mmorealms.module.core.backend.common.command;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.core.backend.common.config.CoreConfig;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = "cache_commit")
public class CommitCacheCommand extends BackendCommand {

	private @Inject CoreConfig config;

	public CommitCacheCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		sendMessage(sender, config.lang.commitingCache);

		for (DatabaseLoader<?, ?, ?> databaseLoader : SyncedDatabaseLoader.getALL()) {
			databaseLoader.getCache().saveCache(false);
		}

		sendMessage(sender, config.lang.cacheCommited);
	}
}
