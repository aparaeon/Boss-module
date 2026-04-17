package gg.mmorealms.module.core.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.module.core.common.command.IDumpCommand;
import gg.mmorealms.module.core.common.files.CommonCoreConfig;
import gg.mmorealms.module.core.velocity.config.CoreConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"dump_proxy"})
public class DumpProxyCommand extends VelocityCommand implements IDumpCommand {

	private @Inject ProxyServer proxy;
	private @Inject CoreConfig config;

	public DumpProxyCommand(CommonCommandManager commandManager) {
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
		return config.lang;
	}

	@Override
	public String getHostname() {
		return "N/A";
	}

	@Override
	public int getPort() {
		return -1;
	}

	@Override
	public int getTotalOnlyPlayers() {
		return this.proxy.getAllPlayers().size();
	}

	@Override
	public int getLocallyOnlyPlayers() {
		return this.getTotalOnlyPlayers();
	}


}
