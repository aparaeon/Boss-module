package gg.mmorealms.module.core.backend.common.command;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.config.CoreConfig;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class UserCommand extends BackendCommand {

	protected @Inject CoreConfig coreConfig;

	public UserCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected final void executePlayer(@NotNull ServerPlayer player, @NotNull List<String> arguments) {
		executeUser(User.get(player), arguments);
	}

	@Override
	protected final void executeConsole(@NotNull MinecraftServer console, @NotNull List<String> arguments) {
		sendMessage(console, coreConfig.lang.commandCanOnlyBeExecuteByPlayer);
	}

	@Override
	protected final void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		if (sender instanceof ServerPlayer) {
			executePlayer((ServerPlayer) sender, arguments);
		} else {
			sendMessage(sender, coreConfig.lang.commandCanOnlyBeExecuteByPlayer);
		}
	}

	protected abstract void executeUser(@NotNull User user, @NotNull List<String> arguments);

	@Override
	protected List<String> recommendPlayersList() {
		return CoreBackendModule.instance().getEngineManager().getPlayersList().getUsernames();
	}
}
