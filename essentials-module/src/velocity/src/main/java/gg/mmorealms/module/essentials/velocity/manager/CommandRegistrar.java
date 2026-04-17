package gg.mmorealms.module.essentials.velocity.manager;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.utils.generic.dto.Pair2;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.command.SimpleCommand;
import gg.mmorealms.module.essentials.velocity.config.SimpleCommandsConfig;

import java.lang.annotation.Annotation;

public class CommandRegistrar {

	public static void register() {
		for (Pair2<String, String> command : SimpleCommandsConfig.INSTANCE.commands) {
			Command commandAnnotation = createAnnotation(command.first());

			SimpleCommand simpleCommand = new SimpleCommand(EssentialsVelocityModule.instance().getCommandManager(), commandAnnotation, command.second());
			EssentialsVelocityModule.instance().getCommandManager().register(commandAnnotation, simpleCommand);
		}
	}

	public static Command createAnnotation(String alias) {
		return new Command() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Command.class;
			}

			@Override
			public String[] aliases() {
				return new String[]{alias};
			}

			@Override
			public String[] arguments() {
				return new String[0];
			}

			@Override
			public OnlyFor onlyFor() {
				return OnlyFor.BOTH;
			}

			@Override
			public Class<? extends CommonCommand> parent() {
				return CommonCommand.class;
			}
		};
	}
}
