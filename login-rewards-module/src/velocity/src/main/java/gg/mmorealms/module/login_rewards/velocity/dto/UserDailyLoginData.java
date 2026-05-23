package gg.mmorealms.module.login_rewards.velocity.dto;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.module.login_rewards.velocity.LoginrewardsVelocityModule;
import gg.mmorealms.module.login_rewards.velocity.manager.loader.UserDailyLoader;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "user_daily_login_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDailyLoginData implements IDatabaseEntry<UUID> {

	@Id
	private UUID uuid;

	@Setter
	private int streak = 0;

	private int pendingClaims = 0;

	@Setter
	private long lastClaimTimestamp = 0L;

	// Cumulative active playtime snapshot taken at observation time, not as-of-window-open.
	@Setter
	private long claimWindowStartPlaytimeMs = 0L;

	@Setter
	private long claimWindowStartTimestamp = 0L;

	public UserDailyLoginData(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public UUID getIdentifier() {
		return uuid;
	}

	@Override
	public UserDailyLoader getLoader() {
		return LoginrewardsVelocityModule.instance().getUserDailyLoader();
	}

	public static UserDailyLoginData getByPlayer(Player player) {
		return getByUUID(player.getUniqueId());
	}

	public static UserDailyLoginData getByUUID(UUID uuid) {
		UserDailyLoginData daily = LoginrewardsVelocityModule.instance().getUserDailyLoader().getByIdentifier(uuid);

		if (daily == null) {
			daily = new UserDailyLoginData(uuid);
			cache(daily);
		}

		return daily;
	}

	public long getWindowPlaytimeMs(long currentActivePlaytimeMs) {
		return Math.max(0L, currentActivePlaytimeMs - claimWindowStartPlaytimeMs);
	}

	public void setPendingClaims(int pendingClaims) {
		this.pendingClaims = Math.max(0, pendingClaims);
	}

	public void resetClaimWindowBaseline(long currentActivePlaytimeMs, long claimWindowStartTimestamp) {
		this.claimWindowStartTimestamp = claimWindowStartTimestamp;
		this.claimWindowStartPlaytimeMs = currentActivePlaytimeMs;
	}

	private static void cache(UserDailyLoginData daily) {
		LoginrewardsVelocityModule.instance().getUserDailyLoader().cache(daily.getIdentifier(), daily);
	}
}
