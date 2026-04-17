package gg.mmorealms.module.warps.backend.common.command;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.warps.backend.common.config.WarpsConfig;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"warps"})
public class WarpsCommand extends BackendCommand {

	private @Inject WarpsConfig config;

	public WarpsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(config.lang.headerWarps);

		for (String name : config.getWarpNames()) {
			stringBuilder.append(config.lang.entryWarps.parse("name", name));
		}

		sendMessage(sender, stringBuilder.toString());
	}
}
