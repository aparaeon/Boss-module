package gg.mmorealms.module.gyms.backend.common.dto.database;

import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IUserGymRecord extends ISavable {
	static @NotNull IUserGymRecord get(@NotNull UUID uuid) {
		IUserGymRecord gymRecord = GymsBackendModule.instance().getGymRecordLoader().getByIdentifier(uuid);

		if (gymRecord == null) {
			gymRecord = new UserGymRecord(uuid);
		}

		return gymRecord;
	}

	static @NotNull IUserGymRecord get(@NotNull Player user) {
		return get(user.getUUID());
	}

	static @NotNull IUserGymRecord get(@NotNull IUser user) {
		return get(user.getUUID());
	}

	static IUserGymRecord get(@NotNull String username) {
		IUser iUser = IUser.getByUsername(username);
		if (iUser == null) {
			return null;
		}
		return get(iUser);
	}

	void set(String id, GymRecord gymRecord);

	default Boolean hasWins(String id, Long amount) {
		GymRecord gymRecord = getGymRecord(id);
		if (gymRecord == null) {
			return false;
		}

		return gymRecord.wins() >= amount;
	}

	default Boolean hasLosses(String id, Long amount) {
		GymRecord gymRecord = getGymRecord(id);
		if (gymRecord == null) {
			return false;
		}

		return gymRecord.losses() >= amount;
	}

	default void logWin(String id) {
		GymRecord currentRecord = getGymRecord(id);
		GymRecord newRecord;
		if (currentRecord == null) {
			newRecord = new GymRecord(1L, 0L);
		} else {
			newRecord = new GymRecord(currentRecord.wins() + 1, currentRecord.losses());
		}

		set(id, newRecord);
	}

	default void logLoss(String id) {
		GymRecord currentRecord = getGymRecord(id);
		GymRecord newRecord;
		if (currentRecord == null) {
			newRecord = new GymRecord(0L, 1L);
		} else {
			newRecord = new GymRecord(currentRecord.wins(), currentRecord.losses() + 1);
		}

		set(id, newRecord);
	}

	@Nullable GymRecord getGymRecord(String id);

	UUID getUUID();

	void delete();
}