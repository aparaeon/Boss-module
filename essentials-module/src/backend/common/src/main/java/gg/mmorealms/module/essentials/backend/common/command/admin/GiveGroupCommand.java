package gg.mmorealms.module.essentials.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.essentials.backend.common.config.EssentialsConfig;
import gg.mmorealms.module.essentials.backend.common.dto.ItemGroup;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = "give_group", arguments = {"target", "group"})
public class GiveGroupCommand extends BackendCommand {

	private @Inject EssentialsConfig config;

	public GiveGroupCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return switch (argument) {
			case "target" -> recommendPlayersList();
			case "group" -> config.itemGroups.keySet().stream().toList();
			default -> new ArrayList<>();
		};
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String targetUsername = arguments.get(0);
		String groupName = arguments.get(1);

		User.executeForUser(targetUsername, (user) -> {
			ItemGroup group = config.itemGroups.get(groupName);
			user.getPlayer().getInventory().add(group.getRandom().getItemStack());
		}, () -> {
			sendMessage(sender, "User not found");
		});

		sendMessage(sender, "Given group");
	}
}
