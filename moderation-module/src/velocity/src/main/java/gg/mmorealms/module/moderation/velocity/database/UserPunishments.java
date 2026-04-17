package gg.mmorealms.module.moderation.velocity.database;

import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.moderation.velocity.ModerationVelocityModule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "user_punishments")
@Getter
@NoArgsConstructor
public class UserPunishments implements IDatabaseEntry<UUID>, ISavable {

	@Id
	private UUID uuid;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("startTime ASC")
	private List<UserBan> bans;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("startTime ASC")
	private List<UserMute> mutes;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("startTime ASC")
	private List<UserWarn> warns;

	public UserPunishments(UUID uuid) {
		this.uuid = uuid;
		this.bans = new ArrayList<>();
		this.mutes = new ArrayList<>();
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public DatabaseLoader<UUID, ?, ?> getLoader() {
		return ModerationVelocityModule.instance().getUserPunishmentsDatabaseLoader();
	}

	public static @Nullable UserPunishments getByUsernameOrUUID(String usernameOrUUID) {
		try {
			UUID uuid = UUID.fromString(usernameOrUUID);
			return getByUUID(uuid);
		} catch (IllegalArgumentException e) {
			Player player = ModerationVelocityModule.instance().getProxy().getPlayer(usernameOrUUID).orElse(null);
			if (player != null) {
				return getByPlayer(player);
			}
		}
		return null;
	}

	public static @NotNull UserPunishments getByPlayer(Player player) {
		return getByUUID(player.getUniqueId());
	}

	public static @NotNull UserPunishments getByUUID(UUID uuid) {
		UserPunishments result = ModerationVelocityModule.instance().getUserPunishmentsDatabaseLoader().getByIdentifier(uuid);

		if (result == null) {
			Logger.debug("Creating missing UserPunishments for player " + uuid);
			result = new UserPunishments(uuid);
			ModerationVelocityModule.instance().getUserPunishmentsDatabaseLoader().cache(uuid, result);
		}

		return result;
	}

	public @Nullable Player getPlayer() { // TODO throw gg.mmorealms.loader.common.exception if player is null
		return ModerationVelocityModule.instance().getProxy().getPlayer(this.uuid).orElse(null);
	}

	public void addMute(UserMute mute) {
		this.mutes.add(mute);

		Player player = getPlayer();

		if (player == null) {
			return;
		}

		player.sendMessage(ModerationVelocityModule.instance().getMiniMessageManager().parse(mute.toString()));
	}

	public void addBan(UserBan ban) {
		this.bans.add(ban);

		Player player = getPlayer();

		if (player == null) {
			return;
		}

		player.disconnect(ModerationVelocityModule.instance().getMiniMessageManager().parse(ban.toString()));
	}

	public void addWarn(UserWarn warn) {
		this.warns.add(warn);

		Player player = getPlayer();

		if (player == null) {
			return;
		}

		player.sendMessage(ModerationVelocityModule.instance().getMiniMessageManager().parse(warn.toString()));
	}

	public void revokeMute() {
		UserMute mute = getMute();

		if (mute == null) {
			return;
		}

		mute.setActive(false);

		Player player = getPlayer();

		if (player == null) {
			return;
		}

		player.sendMessage(ModerationVelocityModule.instance().getMiniMessageManager().parse(
				ModerationVelocityModule.instance().getConfig().lang.muteRevoked
		));
	}

	public void revokeBan() {
		UserBan ban = getBan();

		if (ban == null) {
			return;
		}

		ban.setActive(false);
	}

	public boolean isBanned() {
		UserBan ban = getBan();

		if (ban == null) {
			return false;
		}

		return ban.isActive();
	}

	public boolean isMuted() {
		UserMute mute = getMute();

		if (mute == null) {
			return false;
		}

		return mute.isActive();
	}

	public @Nullable UserMute getMute() {
		if (this.getMutes().isEmpty()) {
			return null;
		}
		return this.getMutes().getLast();
	}

	public @Nullable UserBan getBan() {
		if (this.getBans().isEmpty()) {
			return null;
		}
		return this.getBans().getLast();
	}

	public List<GenericUserPunishment> getPunishments() {
		List<GenericUserPunishment> punishments = new ArrayList<>();

		punishments.addAll(this.getBans());
		punishments.addAll(this.getMutes());
		punishments.addAll(this.getWarns());

		punishments.sort(GenericUserPunishment::compareTo);

		return punishments.reversed();
	}

	public String getUsername() {
		Player player = getPlayer();
		if (player != null) {
			return player.getUsername();
		}
		return uuid + " (OFFLINE)";
	}

}
