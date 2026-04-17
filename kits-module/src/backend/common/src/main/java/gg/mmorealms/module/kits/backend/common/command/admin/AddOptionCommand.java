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

public class AddOptionCommand extends BackendCommand {

	private @Inject KitsConfig config;
	private @Setter String optionType;

	public AddOptionCommand(CommonCommandManager commandManager) {
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
		String option = arguments.get(1);
		Kit kit = KitUtils.get(kitName);
		if (kit == null) {
			sendMessage(sender, config.lang.notFoundKit.parse("name", kitName));

			return;
		}

		switch (optionType) {
			case "lore" -> kit.addLore(option);
			case "command" -> kit.addCommand(option);
		}

		sendMessage(sender, config.lang.successAddOptionMessage
				.parse("option", option)
				.parse("name", kitName));

		if (optionType.equals("command")) {
			sendMessage(sender, config.lang.addCommandWarning);
		}
	}
}
