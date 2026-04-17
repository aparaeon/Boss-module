package gg.mmorealms.module.analytics.velocity.dto;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.loader.common.utils.DateUtils;
import gg.mmorealms.module.analytics.velocity.AnalyticsVelocityModule;
import gg.mmorealms.module.analytics.velocity.dto.event.PurchaseRecordEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetSocketAddress;
import java.util.*;

@Entity(name = "user_stats")
@Getter
@NoArgsConstructor
public class UserStats implements IDatabaseEntry<UUID> {

	@Id
	private UUID uuid;

	private String firstJoinDomain = "";
	private long firstJoinTimestamp = 0L;

	@Setter
	private long lastOnlineTimestamp = 0L;
	private long onlineTime = 0L;
	private long afkTime = 0L;

	@JdbcTypeCode(SqlTypes.JSON)
	private HashMap<Long, Double> purchases = new HashMap<>();

	@Setter
	private String ip = "0.0.0.0";

	@Setter
	private transient long loginTimestamp = 0;

	public UserStats(Player player) {
		this.uuid = player.getUniqueId();

		Optional<InetSocketAddress> optionalVirtualHost = player.getVirtualHost();

		if (optionalVirtualHost.isEmpty()) {
			Logger.warn(new MessageBuilder("Could not find virtual host for {user}")
					.parse("user", player.getUsername())
			);
			return;
		}

		InetSocketAddress virtualHost = optionalVirtualHost.get();
		this.firstJoinDomain = virtualHost.getHostName().split("///")[0];
		this.firstJoinTimestamp = System.currentTimeMillis();
	}

	@Override
	public UUID getIdentifier() {
		return uuid;
	}

	@Override
	public DatabaseLoader<UUID, ?, ?> getLoader() {
		return AnalyticsVelocityModule.instance().getUserStatsLoader();
	}

	public static UserStats getByPlayer(Player player) {
		UserStats userStats = AnalyticsVelocityModule.instance().getUserStatsLoader().getByIdentifier(player.getUniqueId());

		if (userStats == null) {
			userStats = new UserStats(player);
			AnalyticsVelocityModule.instance().getUserStatsLoader().cache(player.identity().uuid(), userStats);
		}

		return userStats;
	}

	public static UserStats getByUUID(UUID uuid) {
		return AnalyticsVelocityModule.instance().getUserStatsLoader().getByIdentifier(uuid);
	}

	public static List<UserStats> getByIP(String ip) {
		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			return session.createQuery("from user_stats where ip = :ip", UserStats.class)
					.setParameter("ip", ip)
					.getResultList();
		} catch (Exception exception) {
			Logger.error(exception);
		}

		return new ArrayList<>();
	}

	public void logout() {
		if (this.loginTimestamp == 0) {
			return;
		}

		long logoutTime = System.currentTimeMillis();

		this.lastOnlineTimestamp = logoutTime;
		this.onlineTime += (logoutTime - this.loginTimestamp);
	}

	public long getOnlineTimeThisSession() {
		if (this.loginTimestamp == 0) {
			return 0L;
		}

		return System.currentTimeMillis() - this.loginTimestamp;
	}

	public long getTimeSinceLastLogin() {
		return System.currentTimeMillis() - this.lastOnlineTimestamp;
	}

	public String getLastTimeOnlineFormatted() {
		if (this.loginTimestamp == 0) {
			return new MessageBuilder("<red>Offline for {duration}")
					.parse("duration", Time.milliseconds(getTimeSinceLastLogin()).toString())
					.parse();
		}

		return new MessageBuilder("<green>Online for {duration}")
				.parse("duration", Time.milliseconds(getOnlineTimeThisSession()).toString())
				.parse();
	}

	public String getOnlineTimeFormatted() {
		return Time.milliseconds(this.onlineTime + getOnlineTimeThisSession() - this.afkTime).toString();
	}

	public void recordPurchase(double amount) {
		new PurchaseRecordEvent(this, amount).fireSync();
		this.purchases.put(System.currentTimeMillis(), amount);
		try {
			this.save();
		} catch (DatabaseSaveException exception) {
			Logger.error(exception);
		}
	}

	public double getPurchasesTotal(long from, long to) {
		double result = 0;

		for (Long timestamp : this.purchases.keySet()) {
			if (from <= timestamp && timestamp <= to) {
				result += this.purchases.get(timestamp);
			}
		}

		return result;
	}

	public double getPurchaseTotalMonthly(int monthDelta) {
		return this.getPurchasesTotal(
				DateUtils.getStartOfMonth(monthDelta),
				System.currentTimeMillis()
		);
	}

	public void recordAfkTime(long time) {
		this.afkTime += time;
	}

	public String getAfkTimeFormatted() {
		return Time.milliseconds(this.afkTime).toString();
	}

}
