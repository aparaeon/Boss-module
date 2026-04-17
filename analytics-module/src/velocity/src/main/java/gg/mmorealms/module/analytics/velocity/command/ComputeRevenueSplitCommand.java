package gg.mmorealms.module.analytics.velocity.command;

import com.google.common.util.concurrent.AtomicDouble;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.loader.common.utils.DateUtils;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;
import gg.mmorealms.module.core.common.utils.NumberUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Command(aliases = {"compute_revenue_split", "get_revenue_split"}, arguments = {"hostname", "split"})
public class ComputeRevenueSplitCommand extends VelocityCommand {
	public ComputeRevenueSplitCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String hostname = arguments.get(0);
		String split = arguments.get(1);

		if (split.endsWith("%")) {
			split = split.substring(0, split.length() - 1);
		}

		double percent;

		try {
			percent = Double.parseDouble(split);
		} catch (NumberFormatException e) {
			Logger.warn("Invalid percentage format"); // TODO Config
			return;
		}

		if (percent > 1) {
			percent /= 100;
		}

		sendMessage(sender, "Querying the database. This might take a while..."); // TODO Config

		double finalPercent = percent;
		AtomicDouble totalRevenue = new AtomicDouble(0);
		AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

		DatabaseManager.instance().executeComplex(
				(session) -> session.createQuery("FROM user_stats WHERE firstJoinDomain = :hostname", UserStats.class)
						.setParameter("hostname", hostname),
				(session) -> session.createQuery("SELECT COUNT(*) FROM user_stats WHERE firstJoinDomain = :hostname", Long.class)
						.setParameter("hostname", hostname),
				(result) -> {
					double userRevenue = result.getPurchasesTotal(DateUtils.getStartOfMonth(1), DateUtils.getStartOfMonth(0));
					totalRevenue.addAndGet(userRevenue);
				},
				(done, total) ->
						sendMessage(sender, new MessageBuilder("[{time}] Query in progress... {done}/{total}")
								.parse("time", Time.milliseconds(System.currentTimeMillis() - startTime.get()))
								.parse("done", done)
								.parse("total", total)
						),
				() -> {
					// TODO Config
					MessageBuilderList template = new MessageBuilderList(List.of(
							"",
							"<gold><b>Domain <gold>{hostname} <aqua>stats:<reset>",

							"<aqua>Total revenue: <white>${revenue}<reset>",
							"<aqua>Revenue split: <white>${revenue_split} <gray>({percent}%)<reset>",
							""
					))
							.parse("hostname", hostname)
							.parse("revenue", NumberUtils.formatNumberWithDecimalPlaces(totalRevenue.get(), 2))
							.parse("percent", finalPercent * 100)
							.parse("revenue_split", NumberUtils.formatNumberWithDecimalPlaces(totalRevenue.get() * finalPercent, 2));

					sendMessage(sender, template);
				},
				1000,
				Time.seconds(1)
		);

	}


}
