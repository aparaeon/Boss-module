package gg.mmorealms.module.essentials.velocity.command.whitelist;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"whitelist"})
@Getter
@Setter
public class WhitelistCommand extends VelocityCommand {

	public WhitelistCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return List.of("on", "off");
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String newSettingString = arguments.getFirst();
		boolean newSetting = newSettingString.equalsIgnoreCase("on");

		if (newSetting) {
			sendMessage(sender, "Whitelist is now enabled."); // TODO Lang
		} else {
			sendMessage(sender, "Whitelist is now disabled."); // TODO Lang
		}

		EssentialsVelocityModule.instance().getConfig().whitelistEnabled = newSetting;
	}


}
