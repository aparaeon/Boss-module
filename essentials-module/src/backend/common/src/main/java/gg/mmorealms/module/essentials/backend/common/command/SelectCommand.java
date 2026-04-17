package gg.mmorealms.module.essentials.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.essentials.backend.common.config.EssentialsConfig;
import gg.mmorealms.module.essentials.backend.common.gui.SelectGUI;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"select"}, onlyFor = Command.OnlyFor.PLAYERS)
public class SelectCommand extends UserCommand {
	private @Inject EssentialsConfig config;

	public SelectCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	private void giveCompass(ServerPlayer player) {
		boolean found = false;

		ItemStack compass = CodecUtils.deserialize(
				ItemStack.CODEC,
				config.selectCompass,
				CodecUtils.CodecErrorProcessor.ofNull());

		for (ItemStack stack : player.getInventory().items) {
			if (stack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString()
					.equals(compass.getComponents().get(DataComponents.CUSTOM_NAME).getString())
					&& stack.getOrDefault(DataComponents.MAX_STACK_SIZE, 0)
					.equals(compass.get(DataComponents.MAX_STACK_SIZE))) {
				found = true;
				break;
			}
		}

		if (found) {
			return;
		}

		player.getInventory().add(compass);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new SelectGUI(user);

		giveCompass(user.getPlayer());
	}
}
