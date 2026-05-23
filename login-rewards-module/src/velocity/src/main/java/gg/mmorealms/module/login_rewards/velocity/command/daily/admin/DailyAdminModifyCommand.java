package gg.mmorealms.module.login_rewards.velocity.command.daily.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.loader.common.utils.MojangUtils;
import gg.mmorealms.module.login_rewards.velocity.LoginrewardsVelocityModule;
import gg.mmorealms.module.login_rewards.velocity.manager.DailyManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command(aliases = {"modify"}, arguments = {"player", "days"}, parent = DailyAdminCommand.class)
public class DailyAdminModifyCommand extends VelocityCommand {

	public DailyAdminModifyCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return recommendPlayersList();
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String target = arguments.get(0);
		int delta = parseDays(arguments.get(1));

		if (delta == 0) {
			sendMessage(sender, LoginrewardsVelocityModule.instance().getConfig().lang.invalidAmount.parse());
			return;
		}

		ScheduleUtils.runTaskAsync(() -> adjust(sender, target, delta));
	}

	private int parseDays(String rawDays) {
		try {
			return Integer.parseInt(rawDays);
		} catch (NumberFormatException ignored) {
			return 0;
		}
	}

	private void adjust(CommandSource sender, String target, int delta) {
		LoginrewardsVelocityModule module = LoginrewardsVelocityModule.instance();
		UUID uuid = module.getUUID(target);

		if (uuid == null) {
			sendMessage(sender, module.getConfig().lang.playerNotFound
				.parse("player", target)
				.parse()
			);
			return;
		}

		DailyManager.StreakAdjustment adjustment = module.getDailyManager().adjustStreak(uuid, delta);

		String deltaSign = adjustment.delta() > 0 ? "+" : "";
		sendMessage(sender, module.getConfig().lang.adminModified
			.parse("player", MojangUtils.getUsernameOrUUID(uuid))
			.parse("previous", adjustment.previousStreak())
			.parse("streak", adjustment.currentStreak())
			.parse("delta_sign", deltaSign)
			.parse("delta", adjustment.delta())
			.parse()
		);
	}
}
