package gg.mmorealms.module.essentials.velocity.command.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.Time;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.dto.Alert;

import java.util.List;

@Command(aliases = {"alert"}, arguments = {"duration", "message"})
public class AlertCommand extends VelocityCommand {

	private @Inject VelocityMiniMessageManager miniMessageManager;

	public AlertCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String durationString = arguments.get(0);
		String message = String.join(" ", arguments.subList(1, arguments.size()));

		String[] split = message.split("\\|");

		Time duration = Time.parse(durationString);
		String title = split[0].strip();
		String description;
		if (split.length == 1) {
			description = "";
		} else {
			description = split[1].strip();
		}

		if (duration == null) {
			sendMessage(sender, "Invalid duration"); // TODO Config
			return;
		}

		Alert alert = new Alert(
				title,
				description,
				System.currentTimeMillis(),
				duration
		);

		EssentialsVelocityModule.instance().getAlertManager().registerAlert(alert);
	}
}
