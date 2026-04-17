package gg.mmorealms.module.economy.velocity.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.economy.velocity.EconomyVelocityModule;
import gg.mmorealms.module.economy.velocity.dto.TopResult;
import lombok.Getter;
import org.hibernate.query.NativeQuery;

import java.util.HashMap;
import java.util.List;

public class TopManager implements Lambda {
	private static final MessageBuilder sqlStatementWithPlaceholders = new MessageBuilder("""
			WITH selected_balances AS (SELECT username                                    AS username,
			                                (balances->>'{currency}')::DOUBLE PRECISION   AS amount,
			                                '{currency}'                                  AS currency
			                           FROM user_balances
			                           JOIN users
			                           ON  user_balances.uuid = users.uuid
			                           AND jsonb_exists(balances, '{currency}')),
			     ranked AS (SELECT username,
			                       amount,
			                       currency,
			                       RANK() OVER (PARTITION BY currency ORDER BY amount DESC) AS rank
			                FROM selected_balances)
			
			SELECT username,
			       amount,
			       currency
			FROM ranked
			WHERE rank <= 10
			ORDER BY currency,
			         amount DESC;
			""");


	@SuppressWarnings("FieldCanBeLocal") // GC Prevention
	private static CancelableTimeTask task;

	@Getter
	private static final HashMap<String, List<TopResult>> topPlayers = new HashMap<>();


	public List<TopResult> getTopPlayers(String currency) {
		return topPlayers.get(currency);
	}

	@Override
	public void run() {
		DatabaseManager.instance().getSessionFactory().inTransaction(session -> {
			try {
				for (String currency : EconomyVelocityModule.instance().getConfig().currenciesToMakeTopFor) {
					String sql = sqlStatementWithPlaceholders.parse("currency", currency).parse();

					//noinspection SqlSourceToSinkFlow
					NativeQuery<TopResult> query = session.createNativeQuery(sql, TopResult.class);
					List<TopResult> result = query.list();

					if (!result.isEmpty()) {
						topPlayers.put(currency, result);
					}
				}
			} catch (Exception exception) {
				Logger.error(exception);
			}
		});
	}

	public void schedule() {
		task = ScheduleUtils.runTaskTimer(this, Time.minutes(10));
	}
}
