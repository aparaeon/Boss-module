package gg.mmorealms.module.kits.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;
import lombok.Setter;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DeleteOptionCommand extends BackendCommand {
	private @Inject KitsConfig config;

	@Setter
	private String optionType;

	public DeleteOptionCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("name")) {
			return KitUtils.getAllNames();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String kitName = arguments.get(0);
		String indexArg = arguments.get(1);

		int index;
		try {
			index = Integer.parseInt(indexArg);
		} catch (NumberFormatException e) {
			sendMessage(sender, config.lang.invalidFormatMessage.parse("arg", "index"));
			return;
		}

		Kit kit = KitUtils.get(kitName);
		if (kit == null) {
			sendMessage(sender, config.lang.notFoundKit.parse("name", kitName));
			return;
		}

		String removedOption = switch (optionType) {
			case "lore" -> kit.removeLore(index);
			case "command" -> kit.removeCommand(index);
			default -> "This is not possible, please contact an administrator if you see this";
		};

		if (removedOption.equals("Index out of Bounds")) {
			sendMessage(sender, config.lang.failedDeleteOptionMessage
					.parse("index", index)
					.parse("optionType", optionType)
					.parse("name", kitName));

			return;
		}

		sendMessage(sender, config.lang.successDeleteOptionMessage
				.parse("option", removedOption)
				.parse("name", kitName));
	}
}
