package gg.mmorealms.module.essentials.backend.common.command.fun;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"hat"}, onlyFor = Command.OnlyFor.PLAYERS)
public class HatCommand extends UserCommand {

	public HatCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		ServerPlayer player = user.getPlayer();

		ItemStack handItem = player.getMainHandItem().copyAndClear();
		ItemStack helmetItem = player.getInventory().armor.get(3).copyAndClear();

		player.getInventory().armor.set(3, handItem);
		player.setItemInHand(InteractionHand.MAIN_HAND, helmetItem);

		user.sendMessage("Hat applied"); // TODO Config
	}
}
