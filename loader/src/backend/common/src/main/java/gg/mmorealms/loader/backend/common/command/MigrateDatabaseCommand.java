
package gg.mmorealms.loader.backend.common.command;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.config.DatabaseConfig;
import gg.mmorealms.loader.common.manager.database.DatabaseConnection;
import gg.mmorealms.loader.common.manager.database.DatabaseMigration;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"migrate_database"})
public class MigrateDatabaseCommand extends BackendCommand {

	public MigrateDatabaseCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		DatabaseConfig targetConfig = SecretsUtils.loadSecretsConfig(DatabaseConfig.class, "migration_target_database_config");
		DatabaseConnection sourceConnection = new DatabaseConnection(CommonLoader.instance().getDatabaseConfig(), 2, 1);
		DatabaseConnection targetConnection = new DatabaseConnection(targetConfig);
		new DatabaseMigration(sourceConnection, targetConnection).start();
		sendMessage(sender, "Migration started");
	}
}
