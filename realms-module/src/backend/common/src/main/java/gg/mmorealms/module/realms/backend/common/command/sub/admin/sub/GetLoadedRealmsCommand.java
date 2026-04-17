package gg.mmorealms.module.realms.backend.common.command.sub.admin.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.command.sub.admin.AdminCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"get_loaded_realms"}, onlyFor = Command.OnlyFor.PLAYERS, parent = AdminCommand.class)
public class GetLoadedRealmsCommand extends UserCommand {

	private @Inject RealmsConfig config;

	public GetLoadedRealmsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		user.sendMessage(new MessageBuilder("{realms_cached_on_server}")
				.parse("realms_cached_on_server", RealmsBackendModule.instance().getRealmsLoader().getCache().keySet()));
	}
}