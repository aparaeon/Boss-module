package gg.mmorealms.module.kits.backend.common.command.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;
import gg.mmorealms.module.kits.backend.common.menu.KitAddMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"add"}, arguments = {"name", "slot", "cooldown"}, parent = AdminCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class AddCommand extends UserCommand {
	private @Inject KitsConfig config;

	public AddCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String name = arguments.get(0);
		String slotArg = arguments.get(1);
		String cooldownArg = arguments.get(2);

		int slot;
		try {
			slot = Integer.parseInt(slotArg);
		} catch (NumberFormatException e) {
			user.sendMessage(config.lang.invalidFormatMessage.parse("arg", "slot"));
			return;
		}

		Time cooldown = Time.parse(cooldownArg);
		if (cooldown == null) {
			user.sendMessage(config.lang.invalidFormatMessage.parse("arg", "cooldown"));
			return;
		}

		ServerPlayer player = user.getPlayer();

		KitAddMenuProvider provider = KitAddMenuProvider.createKit(name, slot, cooldown);

		player.openMenu(provider);
	}

}
