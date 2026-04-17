package gg.mmorealms.module.essentials.backend.common.command.fun;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.essentials.backend.common.gui.menu_provider.VirtualEnchantingTableMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"enchanting_table", "virtual_enchanting_table", "enchantingtable", "virtualenchantingtable"}, onlyFor = Command.OnlyFor.PLAYERS)
public class EnchantingTableCommand extends UserCommand {

	public EnchantingTableCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		ServerPlayer player = user.getPlayer();

		player.openMenu(new VirtualEnchantingTableMenuProvider());
	}
}
