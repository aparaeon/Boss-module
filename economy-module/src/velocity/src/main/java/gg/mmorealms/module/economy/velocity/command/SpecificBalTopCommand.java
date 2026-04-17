package gg.mmorealms.module.economy.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.economy.velocity.EconomyVelocityModule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"specific"}, arguments = {"currency"}, parent = BalTopCommand.class)
public class SpecificBalTopCommand extends BalTopCommand {
	public SpecificBalTopCommand(CommonCommandManager commandManager) {
		super(commandManager);
		this.setSpecific(true);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return EconomyVelocityModule.instance().getConfig().currenciesToMakeTopFor;
	}
}
