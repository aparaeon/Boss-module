package gg.mmorealms.module.homes.backend.common.dto;

import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.homes.backend.common.HomesBackendModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface IHomes extends ISavable {
	static @NotNull IHomes getByUUID(UUID uuid) {
		IHomes homes = HomesBackendModule.instance().getHomesLoader().getByIdentifier(uuid);

		if (homes == null) {
			homes = new Homes(uuid);
		}

		return homes;
	}

	static @Nullable IHomes getByUsername(@NotNull String username) {
		IUser user = IUser.getByUsername(username);
		if (user == null) {
			return null;
		}

		return getByUUID(user.getUUID());
	}

	static @NotNull IHomes getByUser(@NotNull IUser user) {
		return getByUUID(user.getUUID());
	}

	Boolean isEmpty();

	List<Home> getHomes();

	Home get(String name);

	Home get(int index) throws IndexOutOfBoundsException;

	void add(@NotNull Home home);

	Boolean remove(String name);

	void removeAll();

	Boolean has(String name);

	int size();

	String toPrettyString();

	List<String> getNames();

}
