package gg.mmorealms.module.core.backend.common.command;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import gg.mmorealms.module.core.backend.common.config.CoreConfig;
import gg.mmorealms.module.core.backend.common.dto.BackendDetails;
import gg.mmorealms.module.core.backend.common.manager.EngineManager;
import gg.mmorealms.module.core.common.command.IDumpCommand;
import gg.mmorealms.module.core.common.files.CommonCoreConfig;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"dump"})
public class DumpCommand extends BackendCommand implements IDumpCommand {

	private @Inject BackendDetails backendDetails;
	private @Inject MinecraftServer server;
	private @Inject EngineManager engineManager;
	private @Inject CoreConfig coreConfig;

	public DumpCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		ScheduleUtils.runTaskAsync(() -> {
			String message = this.generate();
			sendMessage(sender, message);
		});
	}

	@Override
	public CommonCoreConfig.Lang getLang() {
		return coreConfig.lang;
	}

	@Override
	public String getHostname() {
		return backendDetails.getHostname();
	}

	@Override
	public int getPort() {
		return backendDetails.getPort();
	}

	@Override
	public int getTotalOnlyPlayers() {
		return engineManager.getPlayersList().getList().size();
	}

	@Override
	public int getLocallyOnlyPlayers() {
		return server.getPlayerList().getPlayers().size();
	}
}
